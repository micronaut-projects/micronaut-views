/*
 * Copyright 2017-2026 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.views.rocker;

import com.fizzed.rocker.RockerModel;
import com.fizzed.rocker.TemplateBindException;
import com.fizzed.rocker.compiler.JavaGenerator;
import com.fizzed.rocker.compiler.RockerConfiguration;
import com.fizzed.rocker.compiler.TemplateParser;
import com.fizzed.rocker.model.TemplateModel;
import io.micronaut.core.annotation.Internal;
import io.micronaut.views.ViewsSourceRoots;
import io.micronaut.views.exceptions.ViewRenderingException;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * Compiles the Rocker templates of the views source roots at runtime, with Rocker's own parser and generator
 * ({@code rocker-compiler}) and the platform's Java compiler, and loads them in a loader whose parent is the
 * application's class loader: the current generation's under the development launcher.
 *
 * <p>Rocker's own hot reloading ({@code RockerRuntime.setReloading(true)}) cannot do this under the launcher: its
 * bootstrap is a process-wide singleton of the library tier, reads its template directory from
 * {@code rocker-compiler.conf}, and defines the recompiled classes in a loader whose parent is the library tier,
 * from class files it looks up through that tier. A template importing a class of the application, which the
 * launcher loads in a generation loader, does not link there, and the recompiled classes are not found.</p>
 *
 * <p>Every template of the roots is compiled together, so that a template calling another one links to its new
 * version, and loaded in a new loader after each change. A template that is not under a root is left to the
 * precompiled classes.</p>
 *
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
final class RockerSourceCompiler implements SourceRootsTemplates {

    private static final Logger LOG = LoggerFactory.getLogger(RockerSourceCompiler.class);
    private static final String ROCKER_MARKER = ".rocker.";

    private final ViewsSourceRoots sourceRoots;
    private final ClassLoader classLoader;
    private final String folder;
    /**
     * How many changes were reported: a compilation started before the latest change is compiled again.
     */
    private final AtomicLong changes = new AtomicLong();
    private @Nullable Path workDirectory;
    private int compilations;
    private volatile @Nullable Compiled compiled;

    /**
     * @param folder The views folder, ending with a slash, such as {@code views/}
     * @param sourceRoots The views source roots
     * @param classLoader The application's class loader, the parent of the compiled templates
     */
    RockerSourceCompiler(String folder, ViewsSourceRoots sourceRoots, ClassLoader classLoader) {
        this.folder = folder.isEmpty() || folder.endsWith("/") ? folder : folder + "/";
        this.sourceRoots = sourceRoots;
        this.classLoader = classLoader;
    }

    /**
     * @param classLoader The class loader to look in
     * @return Whether {@code rocker-compiler} and a Java compiler are present
     */
    static boolean isAvailable(ClassLoader classLoader) {
        try {
            Class.forName("com.fizzed.rocker.compiler.TemplateParser", false, classLoader);
        } catch (ClassNotFoundException | LinkageError e) {
            return false;
        }
        return ToolProvider.getSystemJavaCompiler() != null;
    }

    @Override
    public Optional<RockerModel> model(String templatePath) {
        Compiled current = compile();
        String className = current.templates().get(templatePath);
        if (className == null) {
            return Optional.empty();
        }
        if (current.failure() != null) {
            throw current.failure();
        }
        try {
            Class<?> modelType = Class.forName(className, true, current.loader());
            return Optional.of((RockerModel) modelType.getDeclaredConstructor().newInstance());
        } catch (ReflectiveOperationException | LinkageError e) {
            throw new TemplateBindException(templatePath, className, "Unable to create model for template " + templatePath, e);
        }
    }

    @Override
    public void invalidate() {
        changes.incrementAndGet();
    }

    @Override
    public synchronized void close() {
        compiled = null;
        Path directory = workDirectory;
        workDirectory = null;
        if (directory != null) {
            delete(directory);
        }
    }

    private Compiled compile() {
        Compiled current = compiled;
        if (current != null && current.changes() == changes.get()) {
            return current;
        }
        synchronized (this) {
            current = compiled;
            long reported = changes.get();
            if (current == null || current.changes() != reported) {
                // a change reported while compiling leaves this compilation behind the count: the next lookup
                // compiles again
                current = compileTemplates(reported);
                compiled = current;
            }
            return current;
        }
    }

    private Compiled compileTemplates(long reported) {
        Map<String, Path> sources = findTemplates();
        if (sources.isEmpty()) {
            return new Compiled(reported, Map.of(), classLoader, null);
        }
        Map<String, String> templates = new LinkedHashMap<>();
        Path output;
        try {
            output = nextOutput();
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to create a directory to compile Rocker templates into", e);
        }
        Path javaDirectory = output.resolve("java");
        Path classDirectory = output.resolve("classes");
        RockerConfiguration configuration = new RockerConfiguration();
        configuration.setTemplateDirectory(javaDirectory.toFile());
        configuration.setOutputDirectory(javaDirectory.toFile());
        configuration.setClassDirectory(classDirectory.toFile());
        List<File> javaFiles = new ArrayList<>(sources.size());
        try {
            Files.createDirectories(classDirectory);
            TemplateParser parser = new TemplateParser(configuration);
            JavaGenerator generator = new JavaGenerator(configuration);
            for (Map.Entry<String, Path> source : sources.entrySet()) {
                String templatePath = source.getKey();
                int slash = templatePath.lastIndexOf('/');
                String packageName = slash < 0 ? "" : templatePath.substring(0, slash).replace('/', '.');
                TemplateModel model = parser.parse(source.getValue().toFile(), packageName);
                templates.put(templatePath, packageName.isEmpty() ? model.getName() : packageName + "." + model.getName());
                javaFiles.add(generator.generate(model));
            }
        } catch (Exception e) {
            return failed(reported, templates, sources, "Rocker template parsing failed: " + e.getMessage(), e);
        }
        String errors = javac(javaFiles, classDirectory);
        if (errors != null) {
            return failed(reported, templates, sources, "Rocker template compilation failed:" + errors, null);
        }
        LOG.debug("Compiled {} Rocker template(s) of the views source roots into {}", templates.size(), classDirectory);
        try {
            return new Compiled(reported, Map.copyOf(templates), new TemplateClassLoader(classDirectory.toUri().toURL(), classLoader), null);
        } catch (MalformedURLException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Compiled failed(long reported, Map<String, String> templates, Map<String, Path> sources, String message, @Nullable Exception cause) {
        LOG.error("{}", message);
        Map<String, String> all = new LinkedHashMap<>(templates);
        for (String templatePath : sources.keySet()) {
            all.putIfAbsent(templatePath, templatePath);
        }
        return new Compiled(reported, Map.copyOf(all), classLoader, new ViewRenderingException(message, cause));
    }

    /**
     * @return The template path of every Rocker template of the roots, such as {@code views/home.rocker.html}, to
     * its file under the first root that holds it
     */
    private Map<String, Path> findTemplates() {
        Map<String, Path> templates = new LinkedHashMap<>();
        for (Path root : sourceRoots.roots()) {
            if (!Files.isDirectory(root)) {
                continue;
            }
            try (Stream<Path> files = Files.walk(root)) {
                files.filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().contains(ROCKER_MARKER))
                    .sorted()
                    .forEach(file -> {
                        String relative = root.relativize(file).toString().replace(File.separatorChar, '/');
                        // resolved again so that a file reached through a link out of the root is left out
                        sourceRoots.resolve(relative, null)
                            .ifPresent(resolved -> templates.putIfAbsent(folder + relative, resolved));
                    });
            } catch (IOException | UncheckedIOException e) {
                LOG.warn("Unable to list the Rocker templates of {}: {}", root, e.getMessage());
            }
        }
        return templates;
    }

    private @Nullable String javac(List<File> javaFiles, Path classDirectory) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            return " no Java compiler is available: run on a JDK";
        }
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8)) {
            List<String> options = List.of(
                "-classpath", classPath(),
                "-d", classDirectory.toString(),
                "-encoding", StandardCharsets.UTF_8.name(),
                "-proc:none",
                "-nowarn"
            );
            Boolean success = compiler.getTask(null, fileManager, diagnostics, options, null,
                fileManager.getJavaFileObjectsFromFiles(javaFiles)).call();
            if (Boolean.TRUE.equals(success)) {
                return null;
            }
        } catch (IOException e) {
            return " " + e.getMessage();
        }
        StringBuilder errors = new StringBuilder();
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
                JavaFileObject source = diagnostic.getSource();
                errors.append(System.lineSeparator())
                    .append(source == null ? "" : source.getName() + ":" + diagnostic.getLineNumber() + ": ")
                    .append(diagnostic.getMessage(null));
            }
        }
        return errors.toString();
    }

    /**
     * The class path the templates compile against: the URLs of the application's class loader and its parents,
     * which under the launcher include the generation's class directories, then the JVM's class path.
     *
     * @return The class path
     */
    private String classPath() {
        Set<String> entries = new LinkedHashSet<>();
        for (ClassLoader loader = classLoader; loader != null; loader = loader.getParent()) {
            if (loader instanceof URLClassLoader urlClassLoader) {
                for (URL url : urlClassLoader.getURLs()) {
                    if ("file".equals(url.getProtocol())) {
                        try {
                            entries.add(Path.of(url.toURI()).toString());
                        } catch (URISyntaxException | IllegalArgumentException e) {
                            // not a file the compiler can read
                        }
                    }
                }
            }
        }
        String javaClassPath = System.getProperty("java.class.path", "");
        for (String entry : javaClassPath.split(File.pathSeparator)) {
            if (!entry.isEmpty()) {
                entries.add(entry);
            }
        }
        return String.join(File.pathSeparator, entries);
    }

    private synchronized Path nextOutput() throws IOException {
        Path base = workDirectory;
        if (base == null) {
            base = Files.createTempDirectory("micronaut-views-rocker");
            workDirectory = base;
        }
        // every compilation is kept until the engine closes: a model of an earlier one may still be rendering, and
        // loads its nested classes and its text from its directory as it goes
        Path output = base.resolve(Integer.toString(++compilations));
        Files.createDirectories(output);
        return output;
    }

    private static void delete(Path directory) {
        try (Stream<Path> files = Files.walk(directory)) {
            files.sorted(Comparator.reverseOrder()).forEach(file -> {
                try {
                    Files.deleteIfExists(file);
                } catch (IOException e) {
                    // left for the temporary directory's own clean up
                }
            });
        } catch (IOException | UncheckedIOException e) {
            LOG.debug("Unable to delete {}", directory, e);
        }
    }

    /**
     * A compilation of the templates of the roots.
     *
     * @param changes The count of changes reported when it started
     * @param templates The template path of each template to its model class
     * @param loader The loader of the compiled templates
     * @param failure Why the templates did not compile, if they did not
     */
    private record Compiled(long changes, Map<String, String> templates, ClassLoader loader, @Nullable RuntimeException failure) {
    }

    /**
     * Loads the compiled templates ahead of its parent, which may hold the classes the build generated for the
     * same templates, and finds their resources first too: a template reads its text from its {@code $PlainText}
     * class file as a resource.
     */
    private static final class TemplateClassLoader extends URLClassLoader {

        static {
            ClassLoader.registerAsParallelCapable();
        }

        TemplateClassLoader(URL classDirectory, ClassLoader parent) {
            super(new URL[] {classDirectory}, parent);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            synchronized (getClassLoadingLock(name)) {
                Class<?> loaded = findLoadedClass(name);
                if (loaded == null && findResource(name.replace('.', '/') + ".class") != null) {
                    loaded = findClass(name);
                }
                if (loaded == null) {
                    return super.loadClass(name, resolve);
                }
                if (resolve) {
                    resolveClass(loaded);
                }
                return loaded;
            }
        }

        @Override
        public URL getResource(String name) {
            URL resource = findResource(name);
            return resource != null ? resource : super.getResource(name);
        }
    }
}

/*
 * Copyright 2017-2024 original authors
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
package io.micronaut.views.jte;

import gg.jte.CodeResolver;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.TemplateOutput;
import gg.jte.resolve.DirectoryCodeResolver;
import gg.jte.resolve.ResourceCodeResolver;
import io.micronaut.context.watch.ResourceChange;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import io.micronaut.core.io.Writable;
import io.micronaut.views.ReloadableViewsRenderer;
import io.micronaut.views.ViewUtils;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsSourceRoots;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.Arrays;

/**
 * View renderer using JTE.
 *
 * <p>In development mode, templates found under the {@link ViewsSourceRoots views source roots} are compiled from
 * there at runtime, and compiled again on the next render after a change reports them. Templates that are not in
 * the source roots are rendered from their precompiled classes, which follow the class reload, or compiled from
 * the class path when there are none.</p>
 *
 * @param <T> type of input model.
 * @param <R> type of request.
 * @author edward3h
 * @since 3.1.0
 */
public abstract class JteViewsRenderer<T, R> implements ReloadableViewsRenderer<T, R> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JteViewsRenderer.class);
    private static final List<String> EXTENSIONS = Arrays.asList(".jte", ".kte");
    private static final Set<String> WATCHED_EXTENSIONS = Set.of("jte", "kte");
    private final TemplateEngine templateEngine;
    private final @Nullable TemplateEngine sourceEngine;
    private final @Nullable SourceRootsCodeResolver sourceResolver;

    /**
     * @param viewsConfiguration Views configuration
     * @param jteViewsRendererConfiguration JTE specific configuration
     * @param contentType JTE content type of this renderer
     * @param classDirectory When using dynamic templates, where to generate source and class files
     * @deprecated Use {@link #JteViewsRenderer(ViewsConfiguration, JteViewsRendererConfiguration, ContentType, Path, ViewsSourceRoots, ClassLoader)} instead.
     */
    @Deprecated(since = "6.4.0")
    protected JteViewsRenderer(
            ViewsConfiguration viewsConfiguration,
            JteViewsRendererConfiguration jteViewsRendererConfiguration,
            ContentType contentType,
            Path classDirectory) {
        this(viewsConfiguration, jteViewsRendererConfiguration, contentType, classDirectory, ViewsSourceRoots.none(), null);
    }

    /**
     * @param viewsConfiguration Views configuration
     * @param jteViewsRendererConfiguration JTE specific configuration
     * @param contentType JTE content type of this renderer
     * @param classDirectory Where to generate source and class files of templates compiled at runtime
     * @param sourceRoots The views source roots, whose templates are compiled at runtime in development mode
     * @param classLoader The application's class loader, which templates compiled at runtime and the class path
     *                    of their compilation come from in development mode; null for the default
     * @since 6.4.0
     */
    @SuppressWarnings("deprecation")
    protected JteViewsRenderer(
            ViewsConfiguration viewsConfiguration,
            JteViewsRendererConfiguration jteViewsRendererConfiguration,
            ContentType contentType,
            Path classDirectory,
            ViewsSourceRoots sourceRoots,
            @Nullable ClassLoader classLoader) {
        String folder = viewsConfiguration.getFolder();
        if (sourceRoots.isEnabled()) {
            // development mode: the deprecated dynamic settings only add their source directory after the roots
            Path dynamicSource = jteViewsRendererConfiguration.isDynamic() || jteViewsRendererConfiguration.getDynamicSourcePath() != null
                ? findDynamicSourceDirectory(jteViewsRendererConfiguration, folder).orElse(null)
                : null;
            CodeResolver classPath = classLoader != null ? new ResourceCodeResolver(folder, classLoader) : new ResourceCodeResolver(folder);
            sourceResolver = new SourceRootsCodeResolver(sourceRoots, dynamicSource, classPath);
            sourceEngine = classLoader != null
                ? TemplateEngine.create(sourceResolver, classDirectory, contentType, classLoader)
                : TemplateEngine.create(sourceResolver, classDirectory, contentType);
            sourceEngine.setBinaryStaticContent(jteViewsRendererConfiguration.isBinaryStaticContent());
            LOGGER.info("Development mode: compiling the views of the source roots at runtime into {}, and using the precompiled views otherwise.", classDirectory);
            templateEngine = TemplateEngine.createPrecompiled(contentType);
        } else if (jteViewsRendererConfiguration.isDynamic()) {
            sourceResolver = null;
            sourceEngine = null;
            CodeResolver codeResolver = newDynamicCodeResolver(jteViewsRendererConfiguration, folder);
            templateEngine = TemplateEngine.create(codeResolver, classDirectory, contentType);
        } else {
            sourceResolver = null;
            sourceEngine = null;
            LOGGER.info("Using precompiled views.");
            templateEngine = TemplateEngine.createPrecompiled(contentType);
        }
        templateEngine.setBinaryStaticContent(jteViewsRendererConfiguration.isBinaryStaticContent());
    }

    private CodeResolver newDynamicCodeResolver(JteViewsRendererConfiguration jteViewsRendererConfiguration, String folder) {
        Optional<Path> path = findDynamicSourceDirectory(jteViewsRendererConfiguration, folder);
        if (path.isPresent()) {
            LOGGER.info("Using dynamic views loaded from {}", path.get());
            return new DirectoryCodeResolver(path.get());
        }
        LOGGER.info("Dynamic view path not found, using views from classpath.");
        return new ResourceCodeResolver(folder);
    }

    @SuppressWarnings("deprecation")
    private static Optional<Path> findDynamicSourceDirectory(JteViewsRendererConfiguration jteViewsRendererConfiguration, String folder) {
        if (jteViewsRendererConfiguration.getDynamicSourcePath() != null) {
            // explicit setting - trust it
            return Optional.of(Paths.get(jteViewsRendererConfiguration.getDynamicSourcePath()));
        }
        // do we have a conventional 'src' folder?
        try {
            Path src = Paths.get("src");
            if (Files.exists(src)) {
                // micronaut-views convention - templates are in a folder under src/<sourceset>/resources
                try (Stream<Path> search = Files.find(src, 2, ((path, basicFileAttributes) ->
                    path.endsWith("resources") && basicFileAttributes.isDirectory()
                ))) {
                    List<Path> jteSrc = search.map(p -> p.resolve(folder))
                        .filter(Files::exists)
                        .toList();
                    if (jteSrc.size() == 1) {
                        return Optional.of(jteSrc.get(0));
                    }
                }

                // JTE convention src/<sourceset>/jte
                try (Stream<Path> search = Files.find(src, 2, ((path, basicFileAttributes) ->
                    path.endsWith("jte") && basicFileAttributes.isDirectory()
                ))) {
                    List<Path> jteSrc = search
                        .filter(Files::exists)
                        .toList();
                    if (jteSrc.size() == 1) {
                        return Optional.of(jteSrc.get(0));
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.debug("Could not search for the dynamic views source directory", e);
        }
        return Optional.empty();
    }

    @NonNull
    @Override
    public Writable render(@NonNull String viewName,
                           @Nullable T data,
                           @Nullable R request) {
        for (String extension : EXTENSIONS) {
            String name = viewName(viewName, extension);
            TemplateEngine engine = engineFor(name);
            if (engine.hasTemplate(name)) {
                return new JteWritable(engine, name, ViewUtils.modelOf(data), this::decorateOutput);
            }
        }
        return new JteWritable(templateEngine, null, ViewUtils.modelOf(data), this::decorateOutput);
    }

    /**
     * Used during render to construct a JTE TemplateOutput. This is overridable to allow subclasses to specialize
     * the output.
     *
     * @param output
     * @return a TemplateOutput appropriate for the context
     */
    @NonNull
    TemplateOutput decorateOutput(@NonNull TemplateOutput output) {
        return output;
    }

    @Override
    public boolean exists(@NonNull String viewName) {
        return EXTENSIONS.stream()
            .map(x -> viewName(viewName, x))
            .anyMatch(name -> engineFor(name).hasTemplate(name));
    }

    /**
     * The extensions of jte templates, {@code jte} and {@code kte}.
     *
     * @return The extensions
     * @since 6.4.0
     */
    @Override
    public @NonNull Set<String> extensions() {
        return WATCHED_EXTENSIONS;
    }

    /**
     * Has the templates the change reports, and those that use them, compiled again on their next render.
     * Precompiled templates are classes, and follow the class reload instead.
     *
     * @param change The templates that changed or went
     * @since 6.4.0
     */
    @Override
    public void reload(@NonNull ResourceChange change) {
        if (sourceResolver != null) {
            sourceResolver.changed(change);
        }
    }

    private TemplateEngine engineFor(String name) {
        if (sourceEngine == null || sourceResolver == null) {
            return templateEngine;
        }
        // a template of the source roots is compiled from source, any other is precompiled if it can be
        if (sourceResolver.isSource(name) || !templateEngine.hasTemplate(name)) {
            return sourceEngine;
        }
        return templateEngine;
    }

    private String viewName(@NonNull String name, @NonNull String extension) {
        return ViewUtils.normalizeFile(name, extension) + extension;
    }
}

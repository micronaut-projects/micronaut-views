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
package io.micronaut.views.jte;

import gg.jte.CodeResolver;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.TemplateOutput;
import gg.jte.resolve.ResourceCodeResolver;
import io.micronaut.context.watch.ResourceChange;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.io.Writable;
import io.micronaut.views.ViewUtils;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsSourceRoots;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * The JTE templates of development mode, for the development renderers. Templates found under the
 * {@link ViewsSourceRoots views source roots} are compiled from there at runtime, and compiled again on the next
 * render after a change reports them. Templates that are not in the source roots are rendered from their precompiled
 * classes, which follow the class reload, or compiled from the class path when there are none.
 *
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
final class DevelopmentJteTemplates {

    /**
     * The extensions of jte templates, which a change of compiles them again.
     */
    static final Set<String> WATCHED_EXTENSIONS = Set.of("jte", "kte");

    private static final Logger LOGGER = LoggerFactory.getLogger(DevelopmentJteTemplates.class);
    private static final List<String> EXTENSIONS = List.of(".jte", ".kte");

    private final TemplateEngine precompiled;
    private final SourceTemplates sourceTemplates;

    /**
     * @param viewsConfiguration Views configuration
     * @param jteViewsRendererConfiguration JTE specific configuration
     * @param contentType JTE content type of the renderer
     * @param classDirectory Where to generate source and class files of templates compiled at runtime
     * @param sourceRoots The views source roots, enabled
     * @param classLoader The application's class loader, which templates compiled at runtime and the class path of
     *                    their compilation come from; null for the default
     */
    @SuppressWarnings("deprecation")
    DevelopmentJteTemplates(ViewsConfiguration viewsConfiguration,
                            JteViewsRendererConfiguration jteViewsRendererConfiguration,
                            ContentType contentType,
                            Path classDirectory,
                            ViewsSourceRoots sourceRoots,
                            @Nullable ClassLoader classLoader) {
        String folder = viewsConfiguration.getFolder();
        // the deprecated dynamic settings only add their source directory after the roots
        Path dynamicSource = jteViewsRendererConfiguration.isDynamic() || jteViewsRendererConfiguration.getDynamicSourcePath() != null
            ? findDynamicSourceDirectory(jteViewsRendererConfiguration, folder).orElse(null)
            : null;
        CodeResolver classPath = classLoader != null ? new ResourceCodeResolver(folder, classLoader) : new ResourceCodeResolver(folder);
        SourceRootsCodeResolver sourceResolver = new SourceRootsCodeResolver(sourceRoots, dynamicSource, classPath);
        TemplateEngine sourceEngine = classLoader != null
            ? TemplateEngine.create(sourceResolver, classDirectory, contentType, classLoader)
            : TemplateEngine.create(sourceResolver, classDirectory, contentType);
        sourceEngine.setBinaryStaticContent(jteViewsRendererConfiguration.isBinaryStaticContent());
        sourceTemplates = new SourceTemplates(sourceEngine, sourceResolver);
        LOGGER.info("Development mode: compiling the views of the source roots at runtime into {}, and using the precompiled views otherwise.", classDirectory);
        precompiled = TemplateEngine.createPrecompiled(contentType);
    }

    /**
     * @param viewName The view name
     * @param model The model
     * @param outputDecorator Decorates the output of the renderer
     * @return The template, from the source roots or precompiled
     */
    Writable render(String viewName, Map<String, Object> model, Function<TemplateOutput, TemplateOutput> outputDecorator) {
        for (String extension : EXTENSIONS) {
            String name = viewName(viewName, extension);
            SourceTemplates source = sourceFor(name);
            if (source == null) {
                if (precompiled.hasTemplate(name)) {
                    return new JteWritable(precompiled, name, model, outputDecorator);
                }
            } else if (source.hasTemplate(name)) {
                return new SourceTemplatesWritable(source, name, model, outputDecorator);
            }
        }
        return new JteWritable(precompiled, null, model, outputDecorator);
    }

    /**
     * @param viewName The view name
     * @return Whether a template of the source roots or a precompiled one has the view
     */
    boolean exists(String viewName) {
        for (String extension : EXTENSIONS) {
            String name = viewName(viewName, extension);
            SourceTemplates source = sourceFor(name);
            if (source == null ? precompiled.hasTemplate(name) : source.hasTemplate(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Has the templates the change reports, and those that use them, compiled again on their next render.
     * Precompiled templates are classes, and follow the class reload instead.
     *
     * @param change The templates that changed or went
     */
    void changed(ResourceChange change) {
        sourceTemplates.changed(change);
    }

    private @Nullable SourceTemplates sourceFor(String name) {
        // a template of the source roots is compiled from source, any other is precompiled if it can be
        if (sourceTemplates.isSource(name) || !precompiled.hasTemplate(name)) {
            return sourceTemplates;
        }
        return null;
    }

    private static String viewName(String name, String extension) {
        return ViewUtils.normalizeFile(name, extension) + extension;
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
}

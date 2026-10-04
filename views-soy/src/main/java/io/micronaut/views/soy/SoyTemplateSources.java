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
package io.micronaut.views.soy;

import com.google.common.io.Resources;
import com.google.template.soy.SoyFileSet;
import io.micronaut.context.BeanContext;
import io.micronaut.core.annotation.Experimental;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.views.ViewUtils;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsSourceRoots;
import jakarta.inject.Singleton;
import org.jspecify.annotations.NonNull;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Finds the Soy files of the views folder for a {@link SoyFileSetProvider}: in development mode under a
 * {@link ViewsSourceRoots views source root} first, so that the file set is built from the project's sources,
 * and on the class path otherwise.
 *
 * <pre>{@code
 * @Singleton
 * class TemplatesProvider implements SoyFileSetProvider {
 *     private final SoyTemplateSources sources;
 *     ...
 *     public SoyFileSet provideSoyFileSet() {
 *         return sources.add(SoyFileSet.builder(), "home.soy", "layout.soy").build();
 *     }
 * }
 * }</pre>
 *
 * @author graemerocher
 * @since 6.4.0
 */
@Experimental
@Singleton
public final class SoyTemplateSources {

    private final ViewsSourceRoots sourceRoots;
    private final ClassPathResourceLoader resourceLoader;
    private final String folder;

    /**
     * @param viewsConfiguration The views configuration
     * @param sourceRoots The views source roots
     * @param beanContext The context, whose class loader is the application's
     */
    SoyTemplateSources(ViewsConfiguration viewsConfiguration, ViewsSourceRoots sourceRoots, BeanContext beanContext) {
        this.sourceRoots = sourceRoots;
        this.resourceLoader = ClassPathResourceLoader.defaultLoader(beanContext.getClassLoader());
        this.folder = viewsConfiguration.getFolder();
    }

    /**
     * Finds a Soy file of the views folder.
     *
     * @param path The path of the file, relative to the views folder, such as {@code home.soy}
     * @return The file under a source root in development mode, or else the class path resource
     */
    public @NonNull Optional<URL> resolve(@NonNull String path) {
        Optional<Path> source = sourceRoots.resolve(path, null);
        if (source.isPresent()) {
            try {
                return Optional.of(source.get().toUri().toURL());
            } catch (MalformedURLException e) {
                throw new UncheckedIOException(e);
            }
        }
        String name = path.startsWith("/") ? path.substring(1) : path;
        return resourceLoader.getResource(ViewUtils.normalizeFolder(folder) + name);
    }

    /**
     * Adds Soy files of the views folder to a file set.
     *
     * @param builder The builder
     * @param paths The paths, relative to the views folder
     * @return The builder
     * @throws IllegalArgumentException if a file is found neither under a source root nor on the class path
     */
    public SoyFileSet.@NonNull Builder add(SoyFileSet.@NonNull Builder builder, @NonNull String... paths) {
        for (String path : paths) {
            URL url = resolve(path).orElseThrow(() -> new IllegalArgumentException("Soy file [" + path + "] not found in the views folder " + folder));
            // read when the file set is compiled, so that compiling the same file set again reads an edit
            builder.add(Resources.asCharSource(url, StandardCharsets.UTF_8), url.getPath());
        }
        return builder;
    }
}

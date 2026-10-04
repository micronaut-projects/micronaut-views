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
package io.micronaut.views.handlebars;

import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import io.micronaut.core.annotation.Internal;
import io.micronaut.views.ViewsSourceRoots;
import org.jspecify.annotations.Nullable;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

/**
 * The template loader of development mode: a template of the views folder found under a {@link ViewsSourceRoots
 * views source root} is read from there, so that an edit is rendered without a copy step; any other is read from
 * the class path, through the application's class loader.
 *
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
final class SourceRootsTemplateLoader extends ClassPathTemplateLoader {

    private final String folder;
    private final ViewsSourceRoots sourceRoots;
    private final ClassLoader classLoader;

    /**
     * @param folder The views folder, such as {@code views/}
     * @param sourceRoots The source roots
     * @param classLoader The class loader of the class path
     */
    SourceRootsTemplateLoader(String folder, ViewsSourceRoots sourceRoots, ClassLoader classLoader) {
        this.folder = folder;
        this.sourceRoots = sourceRoots;
        this.classLoader = classLoader;
    }

    /**
     * @param location The location of a template, as the renderer compiles it, such as {@code views/home}
     * @return Whether the template is under a source root
     */
    boolean isSource(String location) {
        return source(resolve(location)) != null;
    }

    @Override
    protected URL getResource(String location) {
        Path source = source(location);
        if (source != null) {
            try {
                return source.toUri().toURL();
            } catch (MalformedURLException e) {
                throw new UncheckedIOException(e);
            }
        }
        String name = location.startsWith("/") ? location.substring(1) : location;
        return classLoader.getResource(name);
    }

    private @Nullable Path source(String location) {
        // the location is the prefix, the views folder, the template and the suffix
        String name = location.startsWith("/") ? location.substring(1) : location;
        if (!name.startsWith(folder)) {
            return null;
        }
        Optional<Path> source = sourceRoots.resolve(name.substring(folder.length()), null);
        return source.orElse(null);
    }
}

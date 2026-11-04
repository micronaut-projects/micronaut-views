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
package io.micronaut.views.velocity;

import io.micronaut.core.annotation.Internal;
import io.micronaut.views.ViewsSourceRoots;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.util.ExtProperties;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The resource loader of development mode: a template of the views folder found under a {@link ViewsSourceRoots
 * views source root} is read from there, so that an edit is rendered without a copy step; any other is read from
 * the class path through the application's class loader, the one of the current generation.
 *
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
final class SourceRootsResourceLoader extends ResourceLoader {

    private final String folder;
    private final ViewsSourceRoots sourceRoots;
    private final ClassLoader classLoader;

    /**
     * @param folder The views folder, such as {@code views/}
     * @param sourceRoots The source roots
     * @param classLoader The class loader of the class path
     */
    SourceRootsResourceLoader(String folder, ViewsSourceRoots sourceRoots, ClassLoader classLoader) {
        this.folder = folder;
        this.sourceRoots = sourceRoots;
        this.classLoader = classLoader;
    }

    @Override
    public void init(ExtProperties configuration) {
        // configured by its constructor
    }

    @Override
    public Reader getResourceReader(String name, String encoding) {
        Path source = source(name);
        try {
            if (source != null) {
                return buildReader(Files.newInputStream(source), encoding);
            }
            InputStream stream = classLoader.getResourceAsStream(stripSlash(name));
            if (stream == null) {
                throw new ResourceNotFoundException("Template not found: " + name);
            }
            return buildReader(stream, encoding);
        } catch (IOException e) {
            throw new ResourceNotFoundException("Could not read template " + name, e);
        }
    }

    @Override
    public boolean resourceExists(String name) {
        return source(name) != null || classLoader.getResource(stripSlash(name)) != null;
    }

    @Override
    public boolean isSourceModified(Resource resource) {
        return getLastModified(resource) != resource.getLastModified();
    }

    @Override
    public long getLastModified(Resource resource) {
        Path source = source(resource.getName());
        if (source == null) {
            return 0;
        }
        try {
            return Files.getLastModifiedTime(source).toMillis();
        } catch (IOException e) {
            return 0;
        }
    }

    private @Nullable Path source(String name) {
        String relative = stripSlash(name);
        if (!relative.startsWith(folder)) {
            return null;
        }
        return sourceRoots.resolve(relative.substring(folder.length()), null).orElse(null);
    }

    private static String stripSlash(String name) {
        return name.startsWith("/") ? name.substring(1) : name;
    }
}

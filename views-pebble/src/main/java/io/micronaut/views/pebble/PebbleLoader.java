/*
 * Copyright 2017-2020 original authors
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
package io.micronaut.views.pebble;

import io.micronaut.views.ViewUtils;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsSourceRoots;
import io.pebbletemplates.pebble.error.LoaderException;
import io.pebbletemplates.pebble.loader.ClasspathLoader;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Loader for Pebble templates.
 *
 * @author Ecmel Ercan
 * @since 3.1.0
 */
public class PebbleLoader extends ClasspathLoader {

    private final String extension;
    private final ViewsSourceRoots sourceRoots;

    /**
     * @param views Views Configuration
     * @param pebble Pebble Configuration
     */
    public PebbleLoader(ViewsConfiguration views, PebbleConfiguration pebble) {
        super.setPrefix(ViewUtils.normalizeFolder(views.getFolder()));
        extension = ViewUtils.EXTENSION_SEPARATOR + pebble.getDefaultExtension();
        sourceRoots = ViewsSourceRoots.none();
    }

    /**
     * A loader that reads templates from the views source roots ahead of the class path, as development
     * mode does, and the class path through the given class loader.
     *
     * @param views Views Configuration
     * @param pebble Pebble Configuration
     * @param sourceRoots The views source roots
     * @param classLoader The class loader of the class path
     * @since 6.4.0
     */
    public PebbleLoader(ViewsConfiguration views, PebbleConfiguration pebble, ViewsSourceRoots sourceRoots, ClassLoader classLoader) {
        super(classLoader);
        super.setPrefix(ViewUtils.normalizeFolder(views.getFolder()));
        extension = ViewUtils.EXTENSION_SEPARATOR + pebble.getDefaultExtension();
        this.sourceRoots = sourceRoots;
    }

    @NonNull
    private String normalizeTemplateName(@NonNull String templateName) {
        templateName = templateName.replace("\\", "/");

        if (templateName.startsWith("/")) {
            templateName = templateName.substring(1);
        }

        if (templateName.endsWith(extension)) {
            return templateName;
        }

        int index = templateName.lastIndexOf(ViewUtils.EXTENSION_SEPARATOR);

        if (index < 0) {
            return templateName + extension;
        }

        return templateName;
    }

    @Override
    public String createCacheKey(String templateName) {
        return super.createCacheKey(normalizeTemplateName(templateName));
    }

    @Override
    public boolean resourceExists(String templateName) {
        String name = normalizeTemplateName(templateName);
        return sourceRoots.resolve(name, null).isPresent() || super.resourceExists(name);
    }

    @Override
    public Reader getReader(String cacheKey) {
        Optional<Path> source = sourceRoots.resolve(cacheKey, null);
        if (source.isPresent()) {
            try {
                return Files.newBufferedReader(source.get(), Charset.forName(getCharset()));
            } catch (IOException e) {
                throw new LoaderException(e, "Could not read template " + source.get());
            }
        }
        return super.getReader(cacheKey);
    }
}

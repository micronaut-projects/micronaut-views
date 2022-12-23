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

import io.micronaut.core.annotation.NonNull;
import io.micronaut.views.ViewUtils;
import io.micronaut.views.ViewsConfiguration;
import io.pebbletemplates.pebble.loader.ClasspathLoader;

/**
 * Loader for Pebble templates.
 *
 * @author Ecmel Ercan
 * @since 3.1.0
 */
public class PebbleLoader extends ClasspathLoader {

    private final String extension;

    /**
     * @param views Views Configuration
     * @param pebble Pebble Configuration
     */
    public PebbleLoader(ViewsConfiguration views, PebbleConfiguration pebble) {
        super.setPrefix(ViewUtils.normalizeFolder(views.getFolder()));
        extension = ViewUtils.EXTENSION_SEPARATOR + pebble.getDefaultExtension();
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
        return super.resourceExists(normalizeTemplateName(templateName));
    }
}

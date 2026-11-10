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
package io.micronaut.views.pebble;

import io.micronaut.core.annotation.Internal;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsSourceRoots;
import io.pebbletemplates.pebble.error.LoaderException;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * The Pebble loader of development mode: it reads templates from the views source roots ahead of the class path,
 * and the class path through the application's class loader. The {@link PebbleEngineFactory} uses it only when the
 * source roots are enabled, in development mode; the production {@link PebbleLoader} reads the class path only.
 *
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
final class DevelopmentPebbleLoader extends PebbleLoader {

    private final ViewsSourceRoots sourceRoots;

    /**
     * @param views Views Configuration
     * @param pebble Pebble Configuration
     * @param sourceRoots The views source roots
     * @param classLoader The class loader of the class path
     */
    DevelopmentPebbleLoader(ViewsConfiguration views, PebbleConfiguration pebble, ViewsSourceRoots sourceRoots, ClassLoader classLoader) {
        super(views, pebble, classLoader);
        this.sourceRoots = sourceRoots;
    }

    @Override
    public boolean resourceExists(String templateName) {
        return sourceRoots.resolve(normalizeTemplateName(templateName), null).isPresent() || super.resourceExists(templateName);
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

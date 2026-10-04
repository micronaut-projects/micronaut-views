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
package io.micronaut.views.freemarker;

import freemarker.cache.TemplateLoader;
import io.micronaut.core.annotation.Internal;
import io.micronaut.views.ViewsSourceRoots;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The template loader of development mode: it finds a template under a {@link ViewsSourceRoots views source
 * root}, so that an edit is rendered without a copy step. A template it does not find is left to the class path
 * loader that follows it.
 *
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
final class SourceRootsTemplateLoader implements TemplateLoader {

    private final ViewsSourceRoots sourceRoots;

    /**
     * @param sourceRoots The source roots
     */
    SourceRootsTemplateLoader(ViewsSourceRoots sourceRoots) {
        this.sourceRoots = sourceRoots;
    }

    @Override
    public Object findTemplateSource(String name) {
        // the name is relative to the views folder, with its extension and any locale suffix
        return sourceRoots.resolve(name, null).orElse(null);
    }

    @Override
    public long getLastModified(Object templateSource) {
        try {
            return Files.getLastModifiedTime((Path) templateSource).toMillis();
        } catch (IOException e) {
            return -1;
        }
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        return Files.newBufferedReader((Path) templateSource, Charset.forName(encoding));
    }

    @Override
    public void closeTemplateSource(Object templateSource) {
        // nothing is held open between calls
    }
}

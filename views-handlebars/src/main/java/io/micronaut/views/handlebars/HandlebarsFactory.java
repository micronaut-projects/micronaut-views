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
package io.micronaut.views.handlebars;

import com.github.jknack.handlebars.Handlebars;
import io.micronaut.context.annotation.Factory;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsSourceRoots;

import jakarta.inject.Singleton;

/**
 * Factory for handlebars beans.
 *
 * @author James Kleeh
 * @since 1.1.0
 */
@Factory
public class HandlebarsFactory {

    /**
     * @return The handlebars engine
     * @deprecated Use {@link #handlebars(ViewsConfiguration, ViewsSourceRoots, ClassPathResourceLoader)} instead.
     */
    @Deprecated(since = "6.4.0")
    public Handlebars handlebars() {
        return new Handlebars();
    }

    /**
     * In development mode, the engine reads the templates of the views folder from the views source roots ahead of
     * the class path, and the class path through the application's class loader. Otherwise it reads them from the
     * class path, as before.
     *
     * @param viewsConfiguration The views configuration
     * @param sourceRoots The views source roots
     * @param resourceLoader The class path resource loader
     * @return The handlebars engine
     * @since 6.4.0
     */
    @Singleton
    public Handlebars handlebars(ViewsConfiguration viewsConfiguration,
                                 ViewsSourceRoots sourceRoots,
                                 ClassPathResourceLoader resourceLoader) {
        if (sourceRoots.isEnabled()) {
            return new Handlebars(new SourceRootsTemplateLoader(viewsConfiguration.getFolder(), sourceRoots, resourceLoader.getClassLoader()));
        }
        return new Handlebars();
    }
}

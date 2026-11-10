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

import com.github.jknack.handlebars.Handlebars;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.DevelopmentActive;
import io.micronaut.context.watch.ResourceChange;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.core.util.StringUtils;
import io.micronaut.views.ReloadableViewsRenderer;
import io.micronaut.views.ViewsConfiguration;
import jakarta.inject.Singleton;
import org.jspecify.annotations.NonNull;

import java.util.Set;

/**
 * The Handlebars renderer of development mode: a template of the views source roots exists too, and an edit of the
 * views clears the engine's template cache. The production {@link HandlebarsViewsRenderer} looks templates up on the
 * class path only.
 *
 * @param <T> The model type
 * @param <R> The request type
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
@Singleton
@DevelopmentActive
@Requires(property = HandlebarsViewsRendererConfigurationProperties.PREFIX + ".enabled", notEquals = StringUtils.FALSE)
@Requires(classes = Handlebars.class)
@Replaces(HandlebarsViewsRenderer.class)
final class DevelopmentHandlebarsViewsRenderer<T, R> extends HandlebarsViewsRenderer<T, R> implements ReloadableViewsRenderer<T, R> {

    /**
     * @param viewsConfiguration                   Views Configuration
     * @param resourceLoader                       Resource Loader
     * @param handlebarsViewsRendererConfiguration Handlebars ViewRenderer Configuration.
     * @param handlebars                           Handlebars Engine
     */
    DevelopmentHandlebarsViewsRenderer(ViewsConfiguration viewsConfiguration,
                                       ClassPathResourceLoader resourceLoader,
                                       HandlebarsViewsRendererConfiguration handlebarsViewsRendererConfiguration,
                                       Handlebars handlebars) {
        super(viewsConfiguration, resourceLoader, handlebarsViewsRendererConfiguration, handlebars);
    }

    @Override
    public boolean exists(@NonNull String viewName) {
        if (super.exists(viewName)) {
            return true;
        }
        // a template may be in a source root only
        return handlebars.getLoader() instanceof SourceRootsTemplateLoader loader
            && loader.isSource(viewLocationWithoutExtension(viewName));
    }

    /**
     * None, to watch every views file: a template may use a partial named with any extension.
     *
     * @return No extension
     */
    @Override
    public @NonNull Set<String> extensions() {
        return Set.of();
    }

    /**
     * Clears the template cache of the engine. The default engine has none and compiles a template on every
     * render, so this only matters for an engine configured with a cache.
     *
     * @param change The templates that changed or went
     */
    @Override
    public void reload(@NonNull ResourceChange change) {
        handlebars.getCache().clear();
    }
}

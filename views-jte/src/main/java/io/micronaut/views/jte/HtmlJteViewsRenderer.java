/*
 * Copyright 2017-2023 original authors
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

import gg.jte.ContentType;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsSourceRoots;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jspecify.annotations.Nullable;

import java.nio.file.Paths;

/**
 * JTE renderer constrained to text/html.
 *
 * @param <T> type of input model.
 * @author edward3h
 * @since 3.1.0
 */
@Produces(value = {MediaType.TEXT_HTML})
@Primary
@Singleton
@Requires(classes = HttpRequest.class)
public class HtmlJteViewsRenderer<T> extends JteViewsRenderer<T, HttpRequest<?>> {
    private static final String HTML = "html";

    /**
     * @param viewsConfiguration Views Configuration
     * @param jteViewsRendererConfiguration JTE specific configuration
     * @deprecated Use {@link #HtmlJteViewsRenderer(ViewsConfiguration, JteViewsRendererConfiguration, ViewsSourceRoots, ClassPathResourceLoader)} instead.
     */
    @Deprecated(since = "6.4.0")
    protected HtmlJteViewsRenderer(ViewsConfiguration viewsConfiguration, JteViewsRendererConfiguration jteViewsRendererConfiguration) {
        this(viewsConfiguration, jteViewsRendererConfiguration, ViewsSourceRoots.none(), null);
    }

    /**
     * @param viewsConfiguration Views Configuration
     * @param jteViewsRendererConfiguration JTE specific configuration
     * @param sourceRoots The views source roots, whose templates are compiled at runtime in development mode
     * @param resourceLoader The class path resource loader, whose class loader templates compiled at runtime use
     * @since 6.4.0
     */
    @Inject
    protected HtmlJteViewsRenderer(ViewsConfiguration viewsConfiguration,
                    JteViewsRendererConfiguration jteViewsRendererConfiguration,
                    ViewsSourceRoots sourceRoots,
                    @Nullable ClassPathResourceLoader resourceLoader) {
        super(viewsConfiguration, jteViewsRendererConfiguration, ContentType.Html,
            Paths.get(jteViewsRendererConfiguration.getDynamicPath()).resolve(HTML),
            sourceRoots, resourceLoader != null ? resourceLoader.getClassLoader() : null);
    }
}

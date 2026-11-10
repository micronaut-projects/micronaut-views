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
package io.micronaut.views.handlebars;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.io.ResourceLoader;
import io.micronaut.core.io.Writable;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.core.util.StringUtils;
import io.micronaut.core.util.ArgumentUtils;
import io.micronaut.views.AbstractViewsRenderer;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.exceptions.ViewRenderingException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;

/**
 * Renders Views with Handlebars.java.
 *
 * @author Sergio del Amo
 * @see <a href="https://jknack.github.io/handlebars.java/">https://jknack.github.io/handlebars.java/</a>
 * @since 1.0
 * @param <T> The model type
 * @param <R> The request type
 */
@Requires(property = HandlebarsViewsRendererConfigurationProperties.PREFIX + ".enabled", notEquals = StringUtils.FALSE)
@Requires(classes = Handlebars.class)
@Singleton
public class HandlebarsViewsRenderer<T, R> extends AbstractViewsRenderer<T, R> {

    protected final ViewsConfiguration viewsConfiguration;
    protected final ResourceLoader resourceLoader;
    protected HandlebarsViewsRendererConfiguration handlebarsViewsRendererConfiguration;
    protected Handlebars handlebars;

    /**
     * @param viewsConfiguration                   Views Configuration
     * @param resourceLoader                       Resource Loader
     * @param handlebarsViewsRendererConfiguration Handlebars ViewRenderer Configuration.
     * @param handlebars                           Handlebars Engine
     */
    @Inject
    public HandlebarsViewsRenderer(ViewsConfiguration viewsConfiguration,
                                   ClassPathResourceLoader resourceLoader,
                                   HandlebarsViewsRendererConfiguration handlebarsViewsRendererConfiguration,
                                   Handlebars handlebars) {
        super(handlebarsViewsRendererConfiguration, viewsConfiguration.getFolder(), resourceLoader);
        this.viewsConfiguration = viewsConfiguration;
        this.resourceLoader = resourceLoader;
        this.handlebarsViewsRendererConfiguration = handlebarsViewsRendererConfiguration;
        this.handlebars = handlebars;
    }

    @NonNull
    @Override
    public Writable render(@NonNull String viewName,
                           @Nullable T data,
                           @Nullable R request) {
        ArgumentUtils.requireNonNull("viewName", viewName);
        String location = viewLocationWithoutExtension(viewName);
        Template template;
        try {
            template = handlebars.compile(location);
        } catch (IOException e) {
            throw new ViewRenderingException("Error rendering Handlebars view [" + viewName + "]: " + e.getMessage(), e);
        }
        return (writer) -> {
            try {
                template.apply(data, writer);
            } catch (Throwable e) {
                throw new ViewRenderingException("Error rendering Handlebars view [" + viewName + "]: " + e.getMessage(), e);
            }
        };
    }

}

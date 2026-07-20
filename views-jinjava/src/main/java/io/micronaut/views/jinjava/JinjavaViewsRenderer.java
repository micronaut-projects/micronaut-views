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
package io.micronaut.views.jinjava;

import com.hubspot.jinjava.Jinjava;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.io.ResourceLoader;
import io.micronaut.core.io.Writable;
import io.micronaut.core.util.StringUtils;
import io.micronaut.views.AbstractViewsRenderer;
import io.micronaut.views.ViewUtils;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsRendererConfiguration;
import io.micronaut.views.exceptions.ViewRenderingException;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Renders Views with HubSpot Jinjava.
 *
 * @param <T> The model type
 * @param <R> The request type
 * @since 6.1.1
 */
@Singleton
@Requires(property = JinjavaViewsRendererConfigurationProperties.PREFIX + ".enabled", notEquals = StringUtils.FALSE)
@Requires(classes = Jinjava.class)
public final class JinjavaViewsRenderer<T, R> extends AbstractViewsRenderer<T, R> {

    private final Jinjava jinjava;

    /**
     * @param viewsRendererConfiguration Jinjava Views configuration
     * @param viewsConfiguration Views Configuration
     * @param resourceLoader Resources Loader
     * @param jinjava The configured Jinjava engine
     * @param resourceLocator The scoped Jinjava resource locator
     */
    public JinjavaViewsRenderer(@Named("jinjava") ViewsRendererConfiguration viewsRendererConfiguration,
                                ViewsConfiguration viewsConfiguration,
                                ResourceLoader resourceLoader,
                                Jinjava jinjava,
                                JinjavaResourceLocator resourceLocator) {
        super(viewsRendererConfiguration, viewsConfiguration, resourceLoader);
        this.jinjava = jinjava;
    }

    @Override
    @NonNull
    public Writable render(@NonNull String viewName,
                           @Nullable T data,
                           @Nullable R request) {
        String template = getTemplate(viewName, jinjava.getGlobalConfig().getCharset());
        return writer -> {
            try {
                writer.write(jinjava.render(template, ViewUtils.modelOf(data)));
            } catch (RuntimeException e) {
                throw new ViewRenderingException("Error rendering Jinjava view [" + viewName + "]: " + e.getMessage(), e);
            }
        };
    }
}

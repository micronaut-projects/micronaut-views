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
import com.hubspot.jinjava.loader.ResourceNotFoundException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.io.Writable;
import io.micronaut.core.util.ArgumentUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.views.ViewUtils;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.exceptions.ViewRenderingException;
import jakarta.inject.Singleton;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

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
public final class JinjavaViewsRenderer<T, R> implements ViewsRenderer<T, R> {

    private final Jinjava jinjava;
    private final JinjavaResourceLocator resourceLocator;
    private final String extension;

    /**
     * @param jinjava The configured Jinjava engine
     * @param resourceLocator The scoped Jinjava resource locator
     * @param viewsConfiguration Jinjava Views configuration
     */
    public JinjavaViewsRenderer(Jinjava jinjava,
                                JinjavaResourceLocator resourceLocator,
                                JinjavaViewsRendererConfigurationProperties viewsConfiguration) {
        this.jinjava = jinjava;
        this.resourceLocator = resourceLocator;
        this.extension = viewsConfiguration.getDefaultExtension();
    }

    @Override
    @NonNull
    public Writable render(@NonNull String viewName,
                           @Nullable T data,
                           @Nullable R request) {
        ArgumentUtils.requireNonNull("viewName", viewName);
        String template;
        try {
            template = load(viewName);
        } catch (IOException e) {
            throw new ViewRenderingException("Error rendering Jinjava view [" + viewName + "]: " + e.getMessage(), e);
        }
        return writer -> {
            try {
                writer.write(jinjava.render(template, ViewUtils.modelOf(data)));
            } catch (RuntimeException e) {
                throw new ViewRenderingException("Error rendering Jinjava view [" + viewName + "]: " + e.getMessage(), e);
            }
        };
    }

    @Override
    public boolean exists(@NonNull String viewName) {
        if (viewName == null) {
            return false;
        }
        try {
            return resourceLocator.resourceLoader().getResource(resourceLocator.location(viewLocation(viewName))).isPresent();
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    private String load(String viewName) throws IOException {
        String location = resourceLocator.location(viewLocation(viewName));
        try (InputStream inputStream = resourceLocator.resourceLoader().getResourceAsStream(location)
            .orElseThrow(() -> new ResourceNotFoundException(viewName))) {
            return new String(inputStream.readAllBytes(), jinjava.getGlobalConfig().getCharset());
        }
    }

    private String viewLocation(String name) {
        return ViewUtils.normalizeFile(name, extension) + ViewUtils.EXTENSION_SEPARATOR + extension;
    }
}

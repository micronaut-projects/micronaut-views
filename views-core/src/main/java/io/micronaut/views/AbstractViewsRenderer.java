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
package io.micronaut.views;

import io.micronaut.core.annotation.Internal;
import io.micronaut.core.io.ResourceLoader;
import io.micronaut.core.util.ArgumentUtils;
import io.micronaut.views.exceptions.ViewRenderingException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Base class for view renderers that use a {@link ViewsRendererConfiguration}.
 *
 * @param <T> The model type
 * @param <R> The request type
 */
@Internal
public abstract class AbstractViewsRenderer<T, R> implements ViewsRenderer<T, R> {

    private final ViewsRendererConfiguration configuration;
    private final @Nullable ResourceLoader resourceLoader;
    private final String folder;

    /**
     * @param configuration Renderer configuration
     * @param viewsConfiguration Views Configuration
     * @param resourceLoader Resource Loader
     */
    protected AbstractViewsRenderer(@NonNull ViewsRendererConfiguration configuration,
                                    @NonNull ViewsConfiguration viewsConfiguration,
                                    @Nullable ResourceLoader resourceLoader) {
        this(configuration, viewsConfiguration.getFolder(), resourceLoader);
    }

    /**
     * @param configuration Renderer configuration
     * @param folder The template folder
     */
    protected AbstractViewsRenderer(@NonNull ViewsRendererConfiguration configuration,
                                    @NonNull String folder) {
        this(configuration, folder, null);
    }

    /**
     * @param configuration Renderer configuration
     * @param folder The template folder
     * @param resourceLoader The template resource loader
     */
    protected AbstractViewsRenderer(@NonNull ViewsRendererConfiguration configuration,
                                    @NonNull String folder,
                                    @Nullable ResourceLoader resourceLoader) {
        this.configuration = configuration;
        this.folder = folder;
        this.resourceLoader = resourceLoader;
    }

    @NonNull
    protected final String defaultExtension() {
        return configuration.getDefaultExtension();
    }

    /**
     * Normalizes a view name and appends the configured default extension.
     *
     * @param name The requested view name
     * @return The template name
     */
    @NonNull
    protected final String viewNameWithExtension(@NonNull String name) {
        String extension = defaultExtension();
        return ViewUtils.normalizeFile(name, extension) + ViewUtils.EXTENSION_SEPARATOR + extension;
    }

    /**
     * Normalizes a view name relative to a template folder without appending its extension.
     *
     * @param name The requested view name
     * @return The template location
     */
    @NonNull
    protected final String viewLocationWithoutExtension(@NonNull String name) {
        return folder + ViewUtils.normalizeFile(name, defaultExtension());
    }

    /**
     * Normalizes a view name relative to a template folder and appends the configured default extension.
     *
     * @param name The requested view name
     * @return The template path
     */
    @NonNull
    protected final String viewLocationWithExtension(@NonNull String name) {
        return folder + viewNameWithExtension(name);
    }

    protected @NonNull String getTemplate(@NonNull String viewName, @NonNull Charset charset) {
        ArgumentUtils.requireNonNull("viewName", viewName);
        try {
            return ViewUtils.readResourceAsString(Objects.requireNonNull(resourceLoader), viewLocationWithExtension(viewName), charset);
        } catch (IOException e) {
            throw new ViewRenderingException("Error rendering Jinjava view [" + viewName + "]: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean exists(@NonNull String viewName) {
        String templateName = viewNameWithExtension(viewName);
        return !templateName.contains("//")
            && resourceLoader != null
            && resourceLoader.getResource(folder + templateName).isPresent();
    }
}

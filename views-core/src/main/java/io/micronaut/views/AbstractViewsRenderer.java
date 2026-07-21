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
 * Base class for view renderers backed by a {@link ViewsRendererConfiguration}.
 *
 * <p>Provides the common template-name normalization and resource-location construction used by
 * renderer implementations. The configured default extension is required when resolving a view.
 * A resource loader is optional so renderers that cannot check for template resources can still
 * use the name and location helpers.</p>
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
     * Creates a renderer using the configured views folder and resource loader.
     *
     * @param configuration The renderer configuration, including the default template extension
     * @param viewsConfiguration The shared views configuration
     * @param resourceLoader The resource loader used to locate templates
     */
    protected AbstractViewsRenderer(@NonNull ViewsRendererConfiguration configuration,
                                    @NonNull ViewsConfiguration viewsConfiguration,
                                    @NonNull ResourceLoader resourceLoader) {
        this(configuration, viewsConfiguration.getFolder(), resourceLoader);
    }

    /**
     * Creates a renderer that only uses the shared name and location helpers.
     *
     * @param configuration The renderer configuration, including the default template extension
     * @param folder The template folder prefix
     */
    protected AbstractViewsRenderer(@NonNull ViewsRendererConfiguration configuration,
                                    @NonNull String folder) {
        this(configuration, folder, null);
    }

    /**
     * Creates a renderer using the supplied template folder and optional resource loader.
     *
     * @param configuration The renderer configuration, including the default template extension
     * @param folder The template folder prefix
     * @param resourceLoader The resource loader used to locate templates, if supported by the renderer
     */
    protected AbstractViewsRenderer(@NonNull ViewsRendererConfiguration configuration,
                                    @NonNull String folder,
                                    @Nullable ResourceLoader resourceLoader) {
        this.configuration = configuration;
        this.folder = folder;
        this.resourceLoader = resourceLoader;
    }

    /**
     * Obtains the configured default template extension.
     *
     * @return The configured default extension
     * @throws NullPointerException If no default extension is configured
     */
    protected final @NonNull String defaultExtension() {
        return Objects.requireNonNull(configuration.getDefaultExtension(), "defaultExtension");
    }

    /**
     * Loads the template identified by the view name from the configured resource loader.
     *
     * @param viewName The requested view name, with or without the configured extension
     * @param charset The character set used to decode the template
     * @return The template text
     * @throws ViewRenderingException If the template cannot be loaded
     * @throws NullPointerException If this renderer has no resource loader
     */
    protected @NonNull String getTemplate(@NonNull String viewName, @NonNull Charset charset) {
        ArgumentUtils.requireNonNull("viewName", viewName);
        try {
            return ViewUtils.readResourceAsString(Objects.requireNonNull(resourceLoader), viewLocationWithExtension(viewName), charset);
        } catch (IOException e) {
            throw new ViewRenderingException("Error rendering Jinjava view [" + viewName + "]: " + e.getMessage(), e);
        }
    }

    /**
     * Normalizes a view name and appends the configured default extension.
     *
     * <p>The supplied name may already include the configured extension. The configured extension
     * may include a leading {@code .}; the returned name always contains exactly one separator.</p>
     *
     * @param name The requested view name
     * @return The template name
     */
    @NonNull
    protected final String viewNameWithExtension(@NonNull String name) {
        String extension = defaultExtension();
        String extensionWithoutSeparator = extension.startsWith(ViewUtils.EXTENSION_SEPARATOR)
            ? extension.substring(ViewUtils.EXTENSION_SEPARATOR.length())
            : extension;
        return ViewUtils.normalizeFile(name, extension) + ViewUtils.EXTENSION_SEPARATOR + extensionWithoutSeparator;
    }

    /**
     * Resolves a view name relative to the configured template folder without its extension.
     *
     * @param name The requested view name
     * @return The folder-qualified template location without its extension
     */
    @NonNull
    protected final String viewLocationWithoutExtension(@NonNull String name) {
        return folder + ViewUtils.normalizeFile(name, defaultExtension());
    }

    /**
     * Resolves a view name relative to the configured template folder with its default extension.
     *
     * @param name The requested view name
     * @return The folder-qualified template location with its extension
     */
    @NonNull
    protected final String viewLocationWithExtension(@NonNull String name) {
        return folder + viewNameWithExtension(name);
    }

    /**
     * Determines whether a template resource for the given view name is available.
     *
     * <p>Returns {@code false} when this renderer was created without a resource loader.</p>
     *
     * @param viewName The requested view name, with or without the configured extension
     * @return Whether the corresponding template resource exists
     */
    @Override
    public boolean exists(@NonNull String viewName) {
        return resourceLoader != null && resourceLoader.getResource(folder + viewNameWithExtension(Objects.requireNonNull(viewName))).isPresent();
    }
}

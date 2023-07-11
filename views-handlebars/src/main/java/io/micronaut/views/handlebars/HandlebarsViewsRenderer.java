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
import com.github.jknack.handlebars.Template;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.io.ResourceLoader;
import io.micronaut.core.io.Writable;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.core.util.StringUtils;
import io.micronaut.core.util.ArgumentUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.views.ViewUtils;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.exceptions.ViewRenderingException;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.IOException;

/**
 * Renders Views with with Handlebars.java.
 *
 * @author Sergio del Amo
 * @see <a href="https://jknack.github.io/handlebars.java/">https://jknack.github.io/handlebars.java/</a>
 * @since 1.0
 * @param <T> The model type
 */
@Requires(property = HandlebarsViewsRendererConfigurationProperties.PREFIX + ".enabled", notEquals = StringUtils.FALSE)
@Requires(classes = Handlebars.class)
@Singleton
public class HandlebarsViewsRenderer<T> implements ViewsRenderer<T> {

    protected final ViewsConfiguration viewsConfiguration;
    protected final ResourceLoader resourceLoader;
    protected HandlebarsViewsRendererConfiguration handlebarsViewsRendererConfiguration;
    protected Handlebars handlebars;
    protected String folder;

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
        this.viewsConfiguration = viewsConfiguration;
        this.resourceLoader = resourceLoader;
        this.handlebarsViewsRendererConfiguration = handlebarsViewsRendererConfiguration;
        this.folder = viewsConfiguration.getFolder();
        this.handlebars = handlebars;
    }

    @NonNull
    @Override
    public Writable render(@NonNull String viewName,
                           @Nullable T data,
                           @Nullable HttpRequest<?> request) {
        ArgumentUtils.requireNonNull("viewName", viewName);
        String location = viewLocation(viewName);
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

    @Override
    public boolean exists(@NonNull String viewName) {
        //noinspection ConstantConditions
        if (viewName == null) {
            return false;
        }
        String location = viewLocation(viewName) + ViewUtils.EXTENSION_SEPARATOR + extension();
        return resourceLoader.getResource(location).isPresent();
    }

    private String viewLocation(final String name) {
        return folder + ViewUtils.normalizeFile(name, extension());
    }

    private String extension() {
        return handlebarsViewsRendererConfiguration.getDefaultExtension();
    }

}

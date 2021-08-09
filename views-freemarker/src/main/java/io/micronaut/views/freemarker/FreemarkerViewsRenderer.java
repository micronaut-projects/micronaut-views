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
package io.micronaut.views.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.MalformedTemplateNameException;
import io.micronaut.core.annotation.NonNull;
import freemarker.core.ParseException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.io.Writable;
import io.micronaut.core.util.ArgumentUtils;
import io.micronaut.views.ViewUtils;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.exceptions.ViewRenderingException;

import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.Map;

/**
 * Renders Views with FreeMarker Java template engine.
 *
 * @author Jerónimo López
 * @see <a href= "https://freemarker.apache.org/">freemarker.apache.org</a>
 * @since 1.1
 * @param <T> The model type
 */
@Requires(property = FreemarkerViewsRendererConfigurationProperties.PREFIX + ".enabled", notEquals = "false")
@Requires(classes = Configuration.class)
@Singleton
public class FreemarkerViewsRenderer<T> implements ViewsRenderer<T> {

    protected final ViewsConfiguration viewsConfiguration;
    protected final FreemarkerViewsRendererConfigurationProperties freemarkerMicronautConfiguration;
    protected final String extension;

    /**
     * @param viewsConfiguration      Views Configuration Properties
     * @param freemarkerConfiguration Freemarker Configuration Properties
     */
    @Inject
    public FreemarkerViewsRenderer(ViewsConfiguration viewsConfiguration,
                                   FreemarkerViewsRendererConfigurationProperties freemarkerConfiguration) {
        this.viewsConfiguration = viewsConfiguration;
        this.freemarkerMicronautConfiguration = freemarkerConfiguration;
        this.extension = freemarkerConfiguration.getDefaultExtension();
    }

    @NonNull
    @Override
    public Writable render(@NonNull String viewName, @Nullable T data) {
        ArgumentUtils.requireNonNull("viewName", viewName);
        return (writer) -> {
            Map<String, Object> context = modelOf(data);
            String location = viewLocation(viewName);
            Template template = freemarkerMicronautConfiguration.getTemplate(location);
            try {
                template.process(context, writer);
            } catch (TemplateException e) {
                throw new ViewRenderingException(
                        "Error rendering Freemarker view [" + viewName + "]: " + e.getMessage(), e);
            }
        };
    }

    @Override
    public boolean exists(@NonNull String view) {
        try {
            freemarkerMicronautConfiguration.getTemplate(viewLocation(view));
        } catch (ParseException | MalformedTemplateNameException e) {
            return true;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private String viewLocation(String name) {
        return ViewUtils.normalizeFile(name, extension) +
                EXTENSION_SEPARATOR +
                extension;
    }

}

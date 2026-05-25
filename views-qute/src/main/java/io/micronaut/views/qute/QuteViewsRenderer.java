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
package io.micronaut.views.qute;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.io.Writable;
import io.micronaut.core.util.ArgumentUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.views.ViewUtils;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.exceptions.ViewRenderingException;
import io.quarkus.qute.Engine;
import io.quarkus.qute.Template;
import jakarta.inject.Singleton;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Renders Views with Qute.
 *
 * @param <T> The model type
 * @param <R> The request type
 * @author Sergio del Amo
 * @see <a href="https://quarkus.io/guides/qute">Qute</a>
 * @since 6.1.0
 */
@Requires(property = QuteViewsRendererConfigurationProperties.ENABLED, notEquals = StringUtils.FALSE)
@Requires(classes = Engine.class)
@Singleton
public class QuteViewsRenderer<T, R> implements ViewsRenderer<T, R> {

    private final Engine engine;
    private final QuteTemplateLocator templateLocator;

    /**
     * @param engine Qute engine
     * @param templateLocator Qute template locator
     */
    public QuteViewsRenderer(Engine engine,
                             QuteTemplateLocator templateLocator) {
        this.engine = engine;
        this.templateLocator = templateLocator;
    }

    @Override
    @NonNull
    public Writable render(@NonNull String viewName,
                           @Nullable T data,
                           @Nullable R request) {
        ArgumentUtils.requireNonNull("viewName", viewName);
        Template template = template(viewName);
        return writer -> {
            try {
                writer.write(template.data(ViewUtils.modelOf(data)).render());
            } catch (Throwable e) {
                throw new ViewRenderingException("Error rendering Qute view [" + viewName + "]: " + e.getMessage(), e);
            }
        };
    }

    @Override
    public boolean exists(@NonNull String viewName) {
        return templateLocator.exists(viewName);
    }

    private Template template(String viewName) {
        try {
            Template template = engine.getTemplate(viewName);
            if (template == null) {
                throw new ViewRenderingException("Qute view [" + viewName + "] could not be found");
            }
            return template;
        } catch (ViewRenderingException e) {
            throw e;
        } catch (Throwable e) {
            throw new ViewRenderingException("Error rendering Qute view [" + viewName + "]: " + e.getMessage(), e);
        }
    }
}

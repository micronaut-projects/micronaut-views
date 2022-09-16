/*
 * Copyright 2017-2021 original authors
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
import gg.jte.TemplateEngine;
import gg.jte.TemplateOutput;
import gg.jte.resolve.ResourceCodeResolver;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.io.Writable;
import io.micronaut.http.HttpRequest;
import io.micronaut.views.ViewUtils;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsRenderer;

import java.nio.file.Path;

/**
 * View renderer using JTE.
 *
 * @param <T> type of input model.
 * @author edward3h
 * @since 3.1.0
 */
public abstract class JteViewsRenderer<T> implements ViewsRenderer<T> {
    public static final String DEFAULT_EXTENSION = ".jte";
    private final TemplateEngine templateEngine;

    /**
     * @param viewsConfiguration Views configuration
     * @param jteViewsRendererConfiguration JTE specific configuration
     * @param contentType JTE content type of this renderer
     * @param classDirectory When using dynamic templates, where to generate source and class files
     */
    protected JteViewsRenderer(
            ViewsConfiguration viewsConfiguration,
            JteViewsRendererConfiguration jteViewsRendererConfiguration,
            ContentType contentType,
            Path classDirectory) {

        templateEngine = jteViewsRendererConfiguration.isDynamic() ?
                    TemplateEngine.create(new ResourceCodeResolver(viewsConfiguration.getFolder()), classDirectory, contentType) :
                    TemplateEngine.createPrecompiled(contentType);
        templateEngine.setBinaryStaticContent(jteViewsRendererConfiguration.isBinaryStaticContent());
    }

    @NonNull
    @Override
    public Writable render(@NonNull String viewName,
                           @Nullable T data,
                           @Nullable HttpRequest<?> request) {
        return new JteWritable(templateEngine, viewName(viewName), ViewUtils.modelOf(data), this::decorateOutput);
    }

    /**
     * Used during render to construct a JTE TemplateOutput. This is overridable to allow subclasses to specialize
     * the output.
     *
     * @param output
     * @return a TemplateOutput appropriate for the context
     */
    @NonNull
    TemplateOutput decorateOutput(@NonNull TemplateOutput output) {
        return output;
    }

    @Override
    public boolean exists(@NonNull String viewName) {
        return templateEngine.hasTemplate(viewName(viewName));
    }

    private static String viewName(@NonNull String name) {
        return ViewUtils.normalizeFile(name, DEFAULT_EXTENSION) + DEFAULT_EXTENSION;
    }
}

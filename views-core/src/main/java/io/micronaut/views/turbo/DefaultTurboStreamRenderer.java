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
package io.micronaut.views.turbo;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NextMajorVersion;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.io.Writable;
import io.micronaut.http.HttpRequest;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.ViewsModelDecorator;
import io.micronaut.views.ViewsRendererLocator;
import io.micronaut.views.turbo.http.TurboMediaType;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Optional;

/**
 * {@link io.micronaut.context.annotation.DefaultImplementation} of {@link TurboStreamRenderer}.
 * @author Sergio del Amo
 * @since 3.3.0
 */
@Singleton
@Requires(classes = HttpRequest.class)
public class DefaultTurboStreamRenderer implements TurboStreamRenderer {

    protected final ViewsRendererLocator viewsRendererLocator;
    @Nullable
    @NextMajorVersion("remove the nullability annotation")
    private final ViewsModelDecorator viewsModelDecorator;

    /**
     *
     * @param viewsRendererLocator ViewRendererLocator
     * @param viewsModelDecorator Views Model Decorator
     */
    @Inject
    public DefaultTurboStreamRenderer(ViewsRendererLocator viewsRendererLocator,
                                      ViewsModelDecorator viewsModelDecorator) {
        this.viewsRendererLocator = viewsRendererLocator;
        this.viewsModelDecorator = viewsModelDecorator;
    }

    /**
     *
     * @param viewsRendererLocator View Renderer Locator
     * @deprecated Use {@link #DefaultTurboStreamRenderer(ViewsRendererLocator, ViewsModelDecorator)} instead.
     */
    @Deprecated(since = "5.2.1", forRemoval = true)
    public DefaultTurboStreamRenderer(ViewsRendererLocator viewsRendererLocator) {
        this(viewsRendererLocator, null);
    }

    @Override
    @NonNull
    public Optional<Writable> render(@NonNull TurboStream.Builder builder,
                                     @Nullable HttpRequest<?> request) {
        return builder.getTemplateView()
                .map(viewName ->  {
                    Object model =  builder.getTemplateModel().orElse(null);
                    ModelAndView<Object> modelAndView = new ModelAndView<>(viewName, model);
                    if (request != null && viewsModelDecorator != null) {
                        viewsModelDecorator.decorate(request, modelAndView);
                    }
                    Object decoratedModel = modelAndView.getModel().orElse(null);
                    return viewsRendererLocator.resolveViewsRenderer(viewName, TurboMediaType.TURBO_STREAM, decoratedModel)
                            .flatMap(renderer -> builder.template(renderer.render(viewName, decoratedModel, request))
                                    .build()
                                    .render());
                })
                .orElseGet(() -> builder.build().render());
    }
}

/*
 * Copyright 2017-2024 original authors
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

import io.micronaut.core.annotation.NextMajorVersion;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.io.Writable;
import io.micronaut.http.HttpRequest;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.TemplatedBuilder;
import io.micronaut.views.ViewsModelDecorator;
import io.micronaut.views.ViewsRendererLocator;
import io.micronaut.views.turbo.http.TurboMediaType;
import jakarta.inject.Inject;

import java.util.Optional;

/**
 * @param <T> The class to be built
 * @author Sergio del Amo
 * @since 3.4.0
 */
public abstract class AbstractTurboRenderer<T extends TemplatedBuilder<?>> {

    private final ViewsRendererLocator viewsRendererLocator;
    private final String mediaType;

    @Nullable
    @NextMajorVersion("remove the nullability annotation")
    private final ViewsModelDecorator viewsModelDecorator;

    /**
     * @param viewsRendererLocator Views renderer Locator
     * @param viewsModelDecorator  Views Model Decorator
     * @param mediaType            Media Type
     */
    @Inject
    protected AbstractTurboRenderer(
        ViewsRendererLocator viewsRendererLocator,
        ViewsModelDecorator viewsModelDecorator,
        String mediaType
    ) {
        this.viewsRendererLocator = viewsRendererLocator;
        this.viewsModelDecorator = viewsModelDecorator;
        this.mediaType = mediaType;
    }

    /**
     * @param viewsRendererLocator Views renderer Locator
     * @param mediaType            Media Type
     */
    @Deprecated(since = "5.2.1", forRemoval = true)
    protected AbstractTurboRenderer(ViewsRendererLocator viewsRendererLocator,
                                    String mediaType) {
        this(viewsRendererLocator, null, mediaType);
    }

    /**
     * @param builder Builder
     * @param request The Request
     * @return An Optional Writable with the builder rendered
     */
    @NonNull
    public Optional<Writable> render(@NonNull T builder,
                                     @Nullable HttpRequest<?> request) {
        return builder.getTemplateView()
            .map(viewName -> {
                Object model = builder.getTemplateModel().orElse(null);
                ModelAndView<Object> modelAndView = new ModelAndView<>(viewName, model);
                if (request != null && viewsModelDecorator != null) {
                    viewsModelDecorator.decorate(request, modelAndView);
                }
                Object decoratedModel = modelAndView.getModel().orElse(null);
                return viewsRendererLocator.resolveViewsRenderer(viewName, TurboMediaType.TURBO_STREAM, decoratedModel)
                    .flatMap(renderer -> builder.template(renderer.render(viewName, decoratedModel, request)).build()
                        .render());
            })
            .orElseGet(() -> builder.build().render());
    }
}

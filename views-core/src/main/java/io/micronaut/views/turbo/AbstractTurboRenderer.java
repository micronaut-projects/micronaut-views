/*
 * Copyright 2017-2022 original authors
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

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.io.Writable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.views.TemplatedBuilder;
import io.micronaut.views.ViewsRendererLocator;

import java.util.Optional;

/**
 * @author Sergio del Amo
 * @since 3.4.0
 * @param <T> The class to be built
 */
public abstract class AbstractTurboRenderer<T extends TemplatedBuilder<?>> {
    private final ViewsRendererLocator viewsRendererLocator;
    private final MediaType mediaType;

    /**
     *
     * @param viewsRendererLocator Views renderer Locator
     * @param mediaType Media Type
     */
    protected AbstractTurboRenderer(ViewsRendererLocator viewsRendererLocator,
                                    MediaType mediaType) {
        this.viewsRendererLocator = viewsRendererLocator;
        this.mediaType = mediaType;
    }

    /**
     *
     * @param builder Builder
     * @param request The Request
     * @return An Optional Writable with the builder rendered
     */
    @NonNull
    public Optional<Writable> render(@NonNull T builder,
                                     @Nullable HttpRequest<?> request) {
        return builder.getTemplateView()
                .map(viewName ->  {
                    Object model =  builder.getTemplateModel().orElse(null);
                    return viewsRendererLocator.resolveViewsRenderer(viewName, mediaType, model)
                            .flatMap(renderer -> builder.template(renderer.render(viewName, model, request))
                                    .build()
                                    .render());
                })
                .orElseGet(() -> builder.build().render());
    }
}

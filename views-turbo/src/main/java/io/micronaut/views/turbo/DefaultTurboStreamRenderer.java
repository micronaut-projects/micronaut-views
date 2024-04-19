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

import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.io.Writable;
import io.micronaut.http.MediaType;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.ModelAndViewRenderer;
import io.micronaut.views.ViewsModelDecorator;
import io.micronaut.views.ViewsRendererLocator;
import jakarta.inject.Singleton;

import java.util.Optional;

/**
 * {@link io.micronaut.context.annotation.DefaultImplementation} of {@link TurboStreamRenderer}.
 *
 * @param <R> The request type
 * @author Sergio del Amo
 * @since 3.3.0
 */
@Singleton
@Internal
final class DefaultTurboStreamRenderer<T, R> implements TurboStreamRenderer<R> {

    private final ModelAndViewRenderer<T, R> modelAndViewRenderer;

    DefaultTurboStreamRenderer(ModelAndViewRenderer<T, R> modelAndViewRenderer) {
        this.modelAndViewRenderer = modelAndViewRenderer;
    }

    @Override
    @NonNull
    public Optional<Writable> render(@NonNull TurboStream.Builder builder,
                                     @Nullable R request) {
        Optional<Writable> optionalWritable = builder.getTemplateView()
            .map(viewName -> new ModelAndView<>(viewName, builder.getTemplateModel().orElse(null)))
            .flatMap(modelAndView -> modelAndViewRenderer.render((ModelAndView<T>) modelAndView, request, MediaType.TEXT_HTML));
        return optionalWritable.isPresent()
            ? optionalWritable
            : builder.build().render();
    }
}

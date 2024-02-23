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
package io.micronaut.views.http;

import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;
import jakarta.inject.Singleton;

import java.util.Optional;

/**
 * Resolves a {@link ModelAndView} body if route match is annotated with {@link View}.
 * @author Sergio del Amo
 * @since 6.0.0
 */
@Singleton
@Internal
final class ModelAndViewResponseBodySwapper implements ResponseBodySwapper<ModelAndView<?>> {

    private static final int ORDER = 30;

    @Override
    @NonNull
    public Optional<ResponseBodySwap<ModelAndView<?>>> swap(@NonNull HttpRequest<?> request, @Nullable HttpResponse<?> response) {
        if (response == null) {
            return Optional.empty();
        }
        Object body = response.body();
        if (body instanceof ModelAndView<?> modelAndView) {
            return Optional.of(new ResponseBodySwap<>(modelAndView, MediaType.TEXT_HTML));
        }
        return response.getAttribute(HttpAttributes.ROUTE_MATCH, AnnotationMetadata.class)
                .flatMap(route -> route.findAnnotation(View.class))
                .map(ann -> modelAndViewOf(ann, body))
                .map(m -> new ResponseBodySwap<>(m, MediaType.TEXT_HTML));
    }

    private <T> ModelAndView<T> modelAndViewOf(@NonNull AnnotationValue<View> an, @Nullable T body) {
        ModelAndView<T> modelAndView = new ModelAndView<>();
        an.stringValue().ifPresent(modelAndView::setView);
        if (body != null) {
            modelAndView.setModel(body);
        }
        return modelAndView;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}

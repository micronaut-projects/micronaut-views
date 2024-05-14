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
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;
import jakarta.inject.Singleton;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Resolves a {@link ModelAndView} body if route match is annotated with {@link View}.
 * @author Sergio del Amo
 * @since 6.0.0
 */
@Singleton
@Internal
final class ModelAndViewResponseBodySwapper implements ResponseBodySwapper<ModelAndView<?>> {

    private static final MediaType UTF8_HTML = new MediaType(MediaType.TEXT_HTML, Map.of(MediaType.CHARSET_PARAMETER, "UTF-8"));
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
        Optional<MediaType> mediaType = resolveMediaType(request, response, body);
        return response.getAttribute(HttpAttributes.ROUTE_MATCH, AnnotationMetadata.class)
                .flatMap(route -> route.findAnnotation(View.class))
                .map(ann -> modelAndViewOf(ann, body))
                .map(m -> new ResponseBodySwap<>(m, mediaType.map(MediaType::toString).orElse(MediaType.TEXT_HTML)));
    }

    private <T> ModelAndView<T> modelAndViewOf(@NonNull AnnotationValue<View> an, @Nullable T body) {
        ModelAndView<T> modelAndView = new ModelAndView<>();
        an.stringValue().ifPresent(modelAndView::setView);
        if (body != null) {
            modelAndView.setModel(body);
        }
        return modelAndView;
    }

    private Optional<MediaType> resolveMediaType(@Nullable HttpRequest<?> request, @NonNull HttpResponse<?> response, @Nullable Object responseBody) {
        Optional<AnnotationMetadata> routeMatch = response.getAttribute(HttpAttributes.ROUTE_MATCH, AnnotationMetadata.class);
        if (routeMatch.isEmpty()) {
            return Optional.of(MediaType.APPLICATION_JSON_TYPE);
        }
        AnnotationMetadata route = routeMatch.get();
        Optional<MediaType> type = request == null
            ? route.getValue(Produces.class, MediaType.class)
            : route.getValue(Produces.class, Argument.listOf(MediaType.class)).orElseGet(Collections::emptyList).stream().filter(mt -> accept(request, mt)).findFirst();

        return type.or(defaultType(responseBody, route));
    }

    private static Supplier<Optional<? extends MediaType>> defaultType(Object responseBody, AnnotationMetadata route) {
        return () -> Optional.of((route.getValue(View.class).isPresent() || responseBody instanceof ModelAndView) ? UTF8_HTML : MediaType.APPLICATION_JSON_TYPE);
    }

    private static boolean accept(HttpRequest<?> request, MediaType mediaType) {
        List<MediaType> accept = request.getHeaders().accept();
        return accept.isEmpty() || accept.stream().anyMatch(p -> p.equals(mediaType));
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}

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

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.*;
import io.micronaut.core.util.ArrayUtils;
import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.ResponseFilter;
import io.micronaut.http.annotation.ServerFilter;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;

/**
 * Changes the response body if any {@link ResponseBodySwapper} resolves a different body.
 *
 * @author Sergio del Amo
 * @since 6.0.0
 */
@Requires(classes = HttpRequest.class)
@ServerFilter(ServerFilter.MATCH_ALL_PATTERN)
@Internal
public class ResponseBodySwapperFilter {

    private final ResponseBodySwapper<?> responsebodySwapper;

    public ResponseBodySwapperFilter(ResponseBodySwapper<?> responsebodySwapper) {
        this.responsebodySwapper = responsebodySwapper;
    }

    /**
     * changes the response body if any {@link ResponseBodySwapper} resolves a different body.
     *
     * @param request  HttpRequest
     * @param response Mutable HTTP Response
     */
    @ResponseFilter
    public void filterResponse(HttpRequest<?> request, MutableHttpResponse<?> response) {
        responsebodySwapper.swap(request, response)
                .ifPresent(responseBodySwap -> {
                    response.body(responseBodySwap.body());
                    if (ArrayUtils.isEmpty(produces(response)) && responseBodySwap.mediaType() != null) {
                        response.contentType(responseBodySwap.mediaType());
                    }
                });
    }

    @Nullable
    private String[] produces(@NonNull HttpResponse<?> response) {
        return response.getAttribute(HttpAttributes.ROUTE_MATCH, AnnotationMetadata.class)
                .flatMap(route -> route.findAnnotation(Produces.class))
                    .map(AnnotationValueResolver::stringValues)
                .orElse(null);
    }
}

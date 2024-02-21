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
package io.micronaut.views;

import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;

import java.util.Collections;
import java.util.Optional;

/**
 * Resolves the response content type for the matched route.
 *
 * @author Tim Yates
 * @since 6.0.0
 */
@Internal
public final class MediaTypeResolution {

    private MediaTypeResolution() {
    }

    private static boolean accept(HttpRequest<?> request, MediaType mediaType) {
        String acceptHeader = request.getHeaders().get(HttpHeaders.ACCEPT);
        return acceptHeader != null && acceptHeader.equalsIgnoreCase(mediaType.toString());
    }

    /**
     * Resolves the response content type for the matched route.
     *
     * @param request  HTTP Request
     * @param response HTTP response
     * @return The resolved content type
     */
    @NonNull
    public static Optional<MediaType> resolveMediaType(@Nullable HttpRequest<?> request, @NonNull HttpResponse<?> response) {
        return response.getAttribute(HttpAttributes.ROUTE_MATCH, AnnotationMetadata.class)
            .map(route ->
                route.getValue(Produces.class, Argument.listOf(MediaType.class))
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .filter(mt -> accept(request, mt))
                    .findFirst()
                    .orElse(MediaType.TEXT_HTML_TYPE)
            );
    }
}

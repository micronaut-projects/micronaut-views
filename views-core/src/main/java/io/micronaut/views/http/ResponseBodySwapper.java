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

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.order.Ordered;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;

import java.util.Optional;

/**
 * A functional interface to swap the response's body with a new one.
 * @param <B> The response body
 * @author Sergio del Amo
 * @since 6.0.0
 */
@FunctionalInterface
public interface ResponseBodySwapper<B> extends Ordered {
    /**
     * Returns a new response body to swap the Response body with or an empty optional.
     * @param request The request
     * @param response The response
     * @return A new response body or an empty optional
     */
    @NonNull
    Optional<ResponseBodySwap<B>> swap(@NonNull HttpRequest<?> request, @Nullable HttpResponse<?> response);
}

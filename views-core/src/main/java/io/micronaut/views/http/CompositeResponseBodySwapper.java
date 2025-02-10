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

import io.micronaut.context.annotation.Primary;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

/**
 * A composite {@link ResponseBodySwapper} that iterates over a list of {@link ResponseBodySwapper} and returns the first non empty response body.
 * @param <B> The response body
 */
@Primary
@Singleton
public class CompositeResponseBodySwapper<B> implements ResponseBodySwapper<B> {
    private final List<ResponseBodySwapper<B>> responseBodySwapperList;

    /**
     *
     * @param responseBodySwapperList Response body swappers
     */
    public CompositeResponseBodySwapper(List<ResponseBodySwapper<B>> responseBodySwapperList) {
        this.responseBodySwapperList = responseBodySwapperList;
    }

    @Override
    @NonNull
    public Optional<ResponseBodySwap<B>> swap(@NonNull HttpRequest<?> request, @Nullable HttpResponse<?> response) {
        return responseBodySwapperList.stream()
                .map(resolver -> resolver.swap(request, response))
                .flatMap(Optional::stream)
                .findFirst();
    }
}

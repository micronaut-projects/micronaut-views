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
package io.micronaut.views.reactive;

import io.micronaut.context.annotation.Type;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.core.io.Writable;
import io.micronaut.core.order.Ordered;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

/**
 * Interface to be implemented by View Engines implementations.
 * @param <T> The model type
 * @param <R> The request type
 * @param <O> The response type
 * @author Sergio del Amo
 * @since 1.0
 */
@Type(ReactiveViewsRenderer.class)
public interface ReactiveViewsRenderer<T, R, O> extends Ordered {

    /**
     * @param viewName view name to be rendered
     * @param data     response body to render it with a view
     * @param request  HTTP request
     * @return A writable where the view will be written to.
     */
    @NonNull
    @SingleResult
    Publisher<O> render(@NonNull String viewName,
                               @Nullable T data,
                               @Nullable R request);

    /**
     * @param viewName view name to be rendered
     * @return true if a template can be found for the supplied view name.
     */
    boolean exists(@NonNull String viewName);
}

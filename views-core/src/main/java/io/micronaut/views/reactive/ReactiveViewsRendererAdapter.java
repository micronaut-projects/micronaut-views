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

import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.core.io.Writable;
import io.micronaut.views.ViewsRenderer;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

/**
 * Adapts from {@link ViewsRenderer} to {@link ReactiveViewsRenderer}.
 * @param <T> The model type
 * @param <R> The request type
 * @author Sergio del Amo
 * @since 1.0
 */
@Internal
class ReactiveViewsRendererAdapter<T, R> implements ReactiveViewsRenderer<T, R, Writable> {

    private final ViewsRenderer<T, R> delegate;

    ReactiveViewsRendererAdapter(ViewsRenderer<T, R> viewsRenderer) {
        this.delegate = viewsRenderer;
    }

    /**
     * @param viewName view name to be rendered
     * @param data     response body to render it with a view
     * @param request  HTTP request
     * @return A writable where the view will be written to.
     */
    @Override
    @NonNull
    @SingleResult
    public Publisher<Writable> render(@NonNull String viewName,
                                      @Nullable T data,
                                      @Nullable R request) {
        return Mono.just(delegate.render(viewName, data, request));
    }

    /**
     * @param viewName view name to be rendered
     * @return true if a template can be found for the supplied view name.
     */
    @Override
    public boolean exists(@NonNull String viewName) {
        return delegate.exists(viewName);
    }

    /**
     *
     * @return The class of the delegate
     */
    public Class<? extends ViewsRenderer> getDelegateClass() {
        return delegate.getClass();
    }
}

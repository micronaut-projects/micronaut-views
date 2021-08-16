/*
 * Copyright 2017-2021 original authors
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

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.io.Writable;
import io.micronaut.http.HttpRequest;

/**
 * Writes the view into {@link Writable}.
 * @author Sergio del Amo
 * @since 3.0.0
 * @param <T> The model type
 */
public interface WritableViewsRenderer<T> extends ViewsRenderer<T> {

    /**
     * @param viewName view name to be rendered
     * @param data     response body to render it with a view
     * @param request  HTTP request
     * @return A writable where the view will be written to.
     */
    @NonNull
    Writable render(@NonNull String viewName, @Nullable T data, @NonNull HttpRequest<?> request);
}

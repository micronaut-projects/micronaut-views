/*
 * Copyright 2017-2020 original authors
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
package io.micronaut.views.model;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.views.ModelAndView;

/**
 * Implementers of {@link ViewModelProcessor} process the {@link ModelAndView} and modify it prior to rendering.
 *
 * @param <T> the type this model processor should apply to
 * @author Sergio del Amo
 * @since 1.0
 */
public interface ViewModelProcessor<T> {

    /**
     * Invoked prior to the view rendering.
     *
     * @param request      The request being processed
     * @param modelAndView The model and view
     */
    void process(
            @NonNull HttpRequest<?> request,
            @NonNull ModelAndView<T> modelAndView);
}

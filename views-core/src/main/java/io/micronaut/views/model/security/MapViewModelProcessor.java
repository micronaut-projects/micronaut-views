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
package io.micronaut.views.model.security;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.model.ViewModelProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class to ease populating a map model and handle immutable maps.
 */
@Internal
public sealed interface MapViewModelProcessor
        extends ViewModelProcessor<Map<String, Object>>
        permits CsrfViewModelProcessor, SecurityViewModelProcessor {
    @Override
    default void process(@NonNull HttpRequest<?> request, @NonNull ModelAndView<Map<String, Object>> modelAndView) {
        Map<String, Object> viewModel = modelAndView.getModel().orElseGet(() -> {
            final Map<String, Object> newModel = new HashMap<>(1);
            modelAndView.setModel(newModel);
            return newModel;
        });
        try {
            populateModel(request, viewModel);
        } catch (UnsupportedOperationException ex) {
            final Map<String, Object> modifiableModel = new HashMap<>(viewModel);
            populateModel(request, modifiableModel);
            modelAndView.setModel(modifiableModel);
        }
    }

    /**
     * method to populate the supplied model map with extra entries.
     * @param req HTTP Request
     * @param model Model map being populated
     */
    void populateModel(
            @NonNull HttpRequest<?> req,
            @NonNull Map<String, Object> model);
}

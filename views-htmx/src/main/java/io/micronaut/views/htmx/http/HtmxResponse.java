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
package io.micronaut.views.htmx.http;

import io.micronaut.core.annotation.Experimental;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.views.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * HTMX Response.
 * @author Sergio del Amo
 * @since 6.0.0
 * @param <T> The model type
 */
@Experimental
public final class HtmxResponse<T> {
    private final List<ModelAndView<T>> modelAndViews;

    private HtmxResponse(List<ModelAndView<T>> modelAndViews) {
        this.modelAndViews = modelAndViews;
    }

    /**
     * @return List of Model and Views
     */
    public List<ModelAndView<T>> getModelAndViews() {
        return modelAndViews;
    }

    /**
     *
     * @return An HTMX Response Builder
     * @param <T> Model Type
     */
    @NonNull
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    /**
     * HTMX Response Builder.
     * @param <T> Model Type
     */
    public static class Builder<T> {
        private final List<ModelAndView<T>> modelAndViews = new ArrayList<>();

        /**
         *
         * @param modelAndView Model and View
         * @return The Builder
         */
        @NonNull
        public Builder<T> modelAndView(ModelAndView<T> modelAndView) {
            this.modelAndViews.add(modelAndView);
            return this;
        }

        /**
         *
         * @return Build an {@link HtmxResponse}
         */
        public HtmxResponse<T> build() {
            return new HtmxResponse<>(modelAndViews);
        }
    }
}

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

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Internal;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.ResponseFilter;
import io.micronaut.http.annotation.ServerFilter;

/**
 * For routes annotated with {@link View} sets the response body to a {@link ModelAndView}.
 *
 * @author Tim Yates
 * @since 6.0.0
 */
@Requires(classes = HttpRequest.class)
@ServerFilter(ServerFilter.MATCH_ALL_PATTERN)
@Internal
public class ModelAndViewFilter {

    /**
     * For routes annotated with {@link View} sets the response body to a {@link ModelAndView}.
     *
     * @param request  HttpRequest
     * @param response Mutable HTTP Responses
     */
    @ResponseFilter
    public void filterResponse(HttpRequest<?> request, MutableHttpResponse<?> response) {
        ModelAndView.of(request, response)
            .ifPresent(builder -> {
                final Object body = response.body();

                if (body instanceof ModelAndView<?> modelAndView1) {
                    modelAndView1.getView().ifPresent(builder::setView);
                    modelAndView1.getModel().ifPresent(builder::setModel);
                } else {
                    builder.setModel(body);
                }
                response.body(builder);
            });
    }
}

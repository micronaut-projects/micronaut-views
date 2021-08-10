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

import io.micronaut.context.annotation.DefaultImplementation;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;

/**
 * Decorates a Views Model.
 * @author Sergio del Amo
 * @since 3.0.0
 */
@DefaultImplementation(DefaultViewsModelDecorator.class)
public interface ViewsModelDecorator {
    /**
     * decorates a model.
     *
     * @param request      The http request this model relates to.
     * @param modelAndView The ModelAndView to be enhanced.
     */
     void decorate(HttpRequest<?> request, @NonNull ModelAndView<?> modelAndView);
}

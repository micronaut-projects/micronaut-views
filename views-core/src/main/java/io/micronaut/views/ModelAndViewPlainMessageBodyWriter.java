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

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import jakarta.inject.Singleton;

/**
 * Renders a {@link ModelAndView} as plain text.
 *
 * @author Tim Yates
 * @since 6.0.0
 */
@Singleton
@Produces(MediaType.TEXT_PLAIN)
public class ModelAndViewPlainMessageBodyWriter extends ModelAndViewMessageBodyWriter {

    public ModelAndViewPlainMessageBodyWriter(ModelAndViewRenderer modelAndViewRenderer) {
        super(modelAndViewRenderer);
    }
}

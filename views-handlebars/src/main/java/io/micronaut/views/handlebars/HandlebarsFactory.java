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
package io.micronaut.views.handlebars;

import com.github.jknack.handlebars.Handlebars;
import io.micronaut.context.annotation.Factory;

import jakarta.inject.Singleton;

/**
 * Factory for handlebars beans.
 *
 * @author James Kleeh
 * @since 1.1.0
 */
@Factory
public class HandlebarsFactory {

    /**
     * @return The handlebars engine
     */
    @Singleton
    public Handlebars handlebars() {
        return new Handlebars();
    }
}

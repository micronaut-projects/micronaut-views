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
package io.micronaut.views.fields;

import io.micronaut.context.annotation.DefaultImplementation;
import io.micronaut.core.annotation.NonNull;
import jakarta.validation.ConstraintViolationException;

@DefaultImplementation(DefaultFieldGenerator.class)
public interface FieldsetGenerator {

    /**
     *
     * @param type A class which should be {@link io.micronaut.core.annotation.Introspected}.
     * @return A Fieldset
     */
    @NonNull
    <T> Fieldset generate(@NonNull Class<T> type);

    /**
     *
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @return A Fieldset
     */
    @NonNull
    Fieldset generate(@NonNull Object instance);


    /**
     *
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param ex A Validation exception
     * @return A Fieldset
     */
    @NonNull
    Fieldset generate(@NonNull Object instance,
                      @NonNull ConstraintViolationException ex);
}

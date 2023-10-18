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
import io.micronaut.core.beans.BeanIntrospection;
import jakarta.validation.ConstraintViolationException;

import java.util.function.BiConsumer;

/**
 * API to generate an HTML Fieldset representation of a give type or instance.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@DefaultImplementation(DefaultFieldGenerator.class)
public interface FieldsetGenerator {
    /**
     *
     * @param type A class which should be {@link io.micronaut.core.annotation.Introspected}.
     * @return A Fieldset
     * @param <T> type
     */
    @NonNull
    <T> Fieldset generate(@NonNull Class<T> type);

    /**
     *
     * @param type A class which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param builderConsumer A biconsumer with the property name and the builder
     * @return A Fieldset
     * @param <T> type
     */
    @NonNull
    <T> Fieldset generate(@NonNull Class<T> type, @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer);

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
     * @param builderConsumer A biconsumer with the property name and the builder
     * @return A Fieldset
     */
    @NonNull
    Fieldset generate(@NonNull Object instance, @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer);

    /**
     *
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param ex A Validation exception
     * @return A Fieldset
     */
    @NonNull
    Fieldset generate(@NonNull Object instance,
                      @NonNull ConstraintViolationException ex);

    /**
     *
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param ex A Validation exception
     * @param builderConsumer A biconsumer with the property name and the builder
     * @return A Fieldset
     */
    @NonNull
    Fieldset generate(@NonNull Object instance,
                      @NonNull ConstraintViolationException ex,
                      @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer);
}

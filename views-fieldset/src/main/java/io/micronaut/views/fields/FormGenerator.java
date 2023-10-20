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

import io.micronaut.core.annotation.NonNull;
import jakarta.validation.ConstraintViolationException;

/**
 * Generates a {@link Form} for a given type representing a form class.
 * @author Sergio del Amo
 * @since 4.1.0
 */
public interface FormGenerator {

    /**
     * @param action Form action attribute
     * @param method Form method attribute
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @return A Form
     */
    @NonNull
    default Form generate(@NonNull String action,
                          @NonNull String method,
                          @NonNull Object instance) {
        return generate(action, method, instance, Message.of("Submit", "default.input.submit.value"));
    }

    /**
     * @param action Form action attribute
     * @param method Form method attribute
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param submitValue input submit
     * @return A Form
     */
    @NonNull
    default Form generate(@NonNull String action,
                          @NonNull String method,
                          @NonNull Object instance,
                          @NonNull Message submitValue) {
        return generate(action, method, instance, new InputSubmitFormElement(submitValue));
    }

    /**
     * @param action Form action attribute
     * @param method Form method attribute
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param inputSubmitFormElement input submit
     * @return A Form
     */
    @NonNull
    Form generate(@NonNull String action,
                  @NonNull String method,
                  @NonNull Object instance,
                  @NonNull InputSubmitFormElement inputSubmitFormElement);

    /**
     * @param action Form action attribute
     * @param method Form method attribute
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param ex A Validation exception
     * @return A Form
     */
    @NonNull
    default Form generate(@NonNull String action,
                  @NonNull String method,
                  @NonNull Object instance,
                  @NonNull ConstraintViolationException ex) {
        return generate(action, method, instance, ex, Message.of("Submit", "default.input.submit.value"));
    }

    /**
     * @param action Form action attribute
     * @param method Form method attribute
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param ex A Validation exception
     * @param submitValue input submit
     * @return A Form
     */
    @NonNull
    default Form generate(@NonNull String action,
                  @NonNull String method,
                  @NonNull Object instance,
                  @NonNull ConstraintViolationException ex,
                  @NonNull Message submitValue) {
        return generate(action, method, instance, ex, new InputSubmitFormElement(submitValue));
    }

    /**
     * @param action Form action attribute
     * @param method Form method attribute
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param ex A Validation exception
     * @param inputSubmitFormElement input submit
     * @return A Form
     */
    @NonNull
    Form generate(@NonNull String action,
                  @NonNull String method,
                  @NonNull Object instance,
                  @NonNull ConstraintViolationException ex,
                  @NonNull InputSubmitFormElement inputSubmitFormElement);

    /**
     *
     * @param action Form action attribute
     * @param method Form method attribute
     * @param type A class which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param <T> type
     * @return A Form
     */
    @NonNull
    default <T> Form generate(@NonNull String action,
                              @NonNull String method,
                              @NonNull Class<T> type) {
        return generate(action, method, type, Message.of("Submit", "default.input.submit.value"));
    }

    /**
     *
     * @param action Form action attribute
     * @param method Form method attribute
     * @param type A class which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param submitValue input submit
     * @param <T> type
     * @return A Form
     */
    @NonNull
    default <T> Form generate(@NonNull String action,
                      @NonNull String method,
                      @NonNull Class<T> type,
                      @NonNull Message submitValue) {
        return generate(action, method, type, new InputSubmitFormElement(submitValue));
    }

    /**
     *
     * @param action Form action attribute
     * @param method Form method attribute
     * @param type A class which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param inputSubmitFormElement input submit
     * @param <T> type
     * @return A Form
     */
    @NonNull
    <T> Form generate(@NonNull String action,
                      @NonNull String method,
                      @NonNull Class<T> type,
                      @NonNull InputSubmitFormElement inputSubmitFormElement);
}

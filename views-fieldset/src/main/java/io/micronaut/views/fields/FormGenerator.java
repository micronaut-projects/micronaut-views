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

import io.micronaut.core.annotation.Experimental;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.views.fields.elements.InputSubmitFormElement;
import io.micronaut.views.fields.messages.Message;
import jakarta.validation.ConstraintViolationException;

import java.util.function.BiConsumer;

/**
 * Generates a {@link Form} for a given type representing a form class.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Experimental
public interface FormGenerator {
    /**
     * Form method `post`.
     */
    String POST = "post";

    /**
     * Default Submit message.
     */
    Message SUBMIT = Message.of("Submit", "default.input.submit.value");

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
        return generate(action, method, instance, SUBMIT);
    }

    /**
     * Generate FORM Post.
     * @param action Form action attribute
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @return A Form
     */
    @NonNull
    default Form generate(@NonNull String action,
                          @NonNull Object instance) {
        return generate(action, instance, SUBMIT);
    }

    /**
     * Generate FORM with fieldset.
     * @param action Form action attribute
     * @param method Form method attribute
     * @param fieldset Fieldset
     * @param inputSubmitFormElement input submit
     * @return A Form
     */
    @NonNull
    Form generateWithFieldset(@NonNull String action,
                              @NonNull String method,
                              @NonNull Fieldset fieldset,
                              @NonNull InputSubmitFormElement inputSubmitFormElement);

    /**
     * Generate FORM with fieldset.
     * @param action Form action attribute
     * @param method Form method attribute
     * @param fieldset Fieldset
     * @param submitValue input submit
     * @return A Form
     */
    @NonNull
    default Form generateWithFieldset(@NonNull String action,
                                      @NonNull String method,
                                      @NonNull Fieldset fieldset,
                                      @NonNull Message submitValue) {
        return generateWithFieldset(action, method, fieldset, new InputSubmitFormElement(submitValue));
    }

    /**
     * Generate Form with fieldset.
     * @param action Form action attribute
     * @param fieldset Fieldset
     * @param submitValue input submit
     * @return A Form
     */
    @NonNull
    default Form generateWithFieldset(@NonNull String action,
                                      @NonNull Fieldset fieldset,
                                      @NonNull Message submitValue) {
        return generateWithFieldset(action, POST, fieldset, submitValue);
    }

    /**
     * Generate Form with fieldset.
     * @param action Form action attribute
     * @param fieldset Fieldset
     * @return A Form
     */
    @NonNull
    default Form generateWithFieldset(@NonNull String action,
                                      @NonNull Fieldset fieldset) {
        return generateWithFieldset(action, POST, fieldset, SUBMIT);
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
     * Generate FORM Post.
     * @param action Form action attribute
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param submitValue input submit
     * @return A Form
     */
    @NonNull
    default Form generate(@NonNull String action,
                          @NonNull Object instance,
                          @NonNull Message submitValue) {
        return generate(action, instance, new InputSubmitFormElement(submitValue));
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
     * Generate FORM Post.
     * @param action Form action attribute
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param inputSubmitFormElement input submit
     * @return A Form
     */
    @NonNull
    default Form generate(@NonNull String action,
                  @NonNull Object instance,
                  @NonNull InputSubmitFormElement inputSubmitFormElement) {
        return generate(action, POST, instance, inputSubmitFormElement);
    }

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
        return generate(action, method, instance, ex, SUBMIT);
    }

    /**
     * Generate POST form.
     * @param action Form action attribute
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param ex A Validation exception
     * @return A Form
     */
    @NonNull
    default Form generate(@NonNull String action,
                          @NonNull Object instance,
                          @NonNull ConstraintViolationException ex) {
        return generate(action, instance, ex, SUBMIT);
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
     * Generate POST form.
     * @param action Form action attribute
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param ex A Validation exception
     * @param submitValue input submit
     * @return A Form
     */
    @NonNull
    default Form generate(@NonNull String action,
                          @NonNull Object instance,
                          @NonNull ConstraintViolationException ex,
                          @NonNull Message submitValue) {
        return generate(action, instance, ex, new InputSubmitFormElement(submitValue));
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
     * Generate POST form.
     * @param action Form action attribute
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param ex A Validation exception
     * @param inputSubmitFormElement input submit
     * @return A Form
     */
    @NonNull
    default Form generate(@NonNull String action,
                          @NonNull Object instance,
                          @NonNull ConstraintViolationException ex,
                          @NonNull InputSubmitFormElement inputSubmitFormElement) {
        return generate(action, POST, instance, ex, inputSubmitFormElement);
    }

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
        return generate(action, method, type, SUBMIT);
    }

    /**
     * Generate POST Form.
     * @param action Form action attribute
     * @param type A class which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param <T> type
     * @return A Form
     */
    @NonNull
    default <T> Form generate(@NonNull String action,
                              @NonNull Class<T> type) {
        return generate(action, type, SUBMIT);
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
     * Generate a POST Form.
     * @param action Form action attribute
     * @param type A class which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param submitValue input submit
     * @param <T> type
     * @return A Form
     */
    @NonNull
    default <T> Form generate(@NonNull String action,
                              @NonNull Class<T> type,
                              @NonNull Message submitValue) {
        return generate(action, type, new InputSubmitFormElement(submitValue));
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

    /**
     * Generate a POST form.
     * @param action Form action attribute
     * @param type A class which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param inputSubmitFormElement input submit
     * @param <T> type
     * @return A Form
     */
    @NonNull
    default <T> Form generate(@NonNull String action,
                      @NonNull Class<T> type,
                      @NonNull InputSubmitFormElement inputSubmitFormElement) {
        return generate(action, POST, type, inputSubmitFormElement);
    }


    /**
     * @param action Form action attribute
     * @param method Form method attribute
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param builderConsumer A BiConsumer with the property name and the builder. It allows to consume a form element builder while the form fieldset is being generated.
     * @return A Form
     * @since 5.1.0
     */
    @NonNull
    default Form generate(@NonNull String action,
                          @NonNull String method,
                          @NonNull Object instance,
                          @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        return generate(action, method, instance, SUBMIT, builderConsumer);
    }

    /**
     * Generate FORM Post.
     * @param action Form action attribute
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param builderConsumer A BiConsumer with the property name and the builder. It allows to consume a form element builder while the form fieldset is being generated.
     * @return A Form
     * @since 5.1.0
     */
    @NonNull
    default Form generate(@NonNull String action,
                          @NonNull Object instance,
                          @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        return generate(action, instance, SUBMIT, builderConsumer);
    }

    /**
     * @param action Form action attribute
     * @param method Form method attribute
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param submitValue input submit
     * @param builderConsumer A BiConsumer with the property name and the builder. It allows to consume a form element builder while the form fieldset is being generated.
     * @return A Form
     * @since 5.1.0
     */
    @NonNull
    default Form generate(@NonNull String action,
                          @NonNull String method,
                          @NonNull Object instance,
                          @NonNull Message submitValue,
                          @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        return generate(action, method, instance, new InputSubmitFormElement(submitValue), builderConsumer);
    }

    /**
     * Generate FORM Post.
     * @param action Form action attribute
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param submitValue input submit
     * @param builderConsumer A BiConsumer with the property name and the builder. It allows to consume a form element builder while the form fieldset is being generated.
     * @return A Form
     * @since 5.1.0
     */
    @NonNull
    default Form generate(@NonNull String action,
                          @NonNull Object instance,
                          @NonNull Message submitValue,
                          @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        return generate(action, instance, new InputSubmitFormElement(submitValue), builderConsumer);
    }

    /**
     * @param action Form action attribute
     * @param method Form method attribute
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param inputSubmitFormElement input submit
     * @param builderConsumer A BiConsumer with the property name and the builder. It allows to consume a form element builder while the form fieldset is being generated.
     * @return A Form
     * @since 5.1.0
     */
    @NonNull
    Form generate(@NonNull String action,
                  @NonNull String method,
                  @NonNull Object instance,
                  @NonNull InputSubmitFormElement inputSubmitFormElement,
                  @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer);

        /**
         * Generate FORM Post.
         * @param action Form action attribute
         * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
         * @param inputSubmitFormElement input submit
         * @param builderConsumer A BiConsumer with the property name and the builder. It allows to consume a form element builder while the form fieldset is being generated.
         * @return A Form
         * @since 5.1.0
         */
    @NonNull
    default Form generate(@NonNull String action,
                          @NonNull Object instance,
                          @NonNull InputSubmitFormElement inputSubmitFormElement,
                          @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        return generate(action, POST, instance, inputSubmitFormElement, builderConsumer);
    }

    /**
     * @param action Form action attribute
     * @param method Form method attribute
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param ex A Validation exception
     * @param builderConsumer A BiConsumer with the property name and the builder. It allows to consume a form element builder while the form fieldset is being generated.
     * @return A Form
     * @since 5.1.0
     */
    @NonNull
    default Form generate(@NonNull String action,
                          @NonNull String method,
                          @NonNull Object instance,
                          @NonNull ConstraintViolationException ex,
                          @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        return generate(action, method, instance, ex, SUBMIT, builderConsumer);
    }

    /**
     * Generate POST form.
     * @param action Form action attribute
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param ex A Validation exception
     * @param builderConsumer A BiConsumer with the property name and the builder. It allows to consume a form element builder while the form fieldset is being generated.
     * @return A Form
     * @since 5.1.0
     */
    @NonNull
    default Form generate(@NonNull String action,
                          @NonNull Object instance,
                          @NonNull ConstraintViolationException ex,
                          @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        return generate(action, instance, ex, SUBMIT, builderConsumer);
    }

    /**
     * @param action Form action attribute
     * @param method Form method attribute
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param ex A Validation exception
     * @param submitValue input submit
     * @param builderConsumer A BiConsumer with the property name and the builder. It allows to consume a form element builder while the form fieldset is being generated.
     * @return A Form
     * @since 5.1.0
     */
    @NonNull
    default Form generate(@NonNull String action,
                          @NonNull String method,
                          @NonNull Object instance,
                          @NonNull ConstraintViolationException ex,
                          @NonNull Message submitValue,
                          @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        return generate(action, method, instance, ex, new InputSubmitFormElement(submitValue), builderConsumer);
    }

    /**
     * Generate POST form.
     * @param action Form action attribute
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param ex A Validation exception
     * @param submitValue input submit
     * @param builderConsumer A BiConsumer with the property name and the builder. It allows to consume a form element builder while the form fieldset is being generated.
     * @return A Form
     * @since 5.1.0
     */
    @NonNull
    default Form generate(@NonNull String action,
                          @NonNull Object instance,
                          @NonNull ConstraintViolationException ex,
                          @NonNull Message submitValue,
                          @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        return generate(action, instance, ex, new InputSubmitFormElement(submitValue), builderConsumer);
    }

    /**
     * @param action Form action attribute
     * @param method Form method attribute
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param ex A Validation exception
     * @param inputSubmitFormElement input submit
     * @param builderConsumer A BiConsumer with the property name and the builder. It allows to consume a form element builder while the form fieldset is being generated.
     * @return A Form
     * @since 5.1.0
     */
    @NonNull
    Form generate(@NonNull String action,
                  @NonNull String method,
                  @NonNull Object instance,
                  @NonNull ConstraintViolationException ex,
                  @NonNull InputSubmitFormElement inputSubmitFormElement,
                  @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer);

    /**
     * Generate POST form.
     * @param action Form action attribute
     * @param instance The Object instance which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param ex A Validation exception
     * @param inputSubmitFormElement input submit
     * @param builderConsumer A BiConsumer with the property name and the builder. It allows to consume a form element builder while the form fieldset is being generated.
     * @return A Form
     * @since 5.1.0
     */
    @NonNull
    default Form generate(@NonNull String action,
                          @NonNull Object instance,
                          @NonNull ConstraintViolationException ex,
                          @NonNull InputSubmitFormElement inputSubmitFormElement,
                          @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        return generate(action, POST, instance, ex, inputSubmitFormElement, builderConsumer);
    }

    /**
     *
     * @param action Form action attribute
     * @param method Form method attribute
     * @param type A class which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param <T> type
     * @param builderConsumer A BiConsumer with the property name and the builder. It allows to consume a form element builder while the form fieldset is being generated.
     * @return A Form
     * @since 5.1.0
     */
    @NonNull
    default <T> Form generate(@NonNull String action,
                              @NonNull String method,
                              @NonNull Class<T> type,
                              @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        return generate(action, method, type, SUBMIT, builderConsumer);
    }

    /**
     * Generate POST Form.
     * @param action Form action attribute
     * @param type A class which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param <T> type
     * @param builderConsumer A BiConsumer with the property name and the builder. It allows to consume a form element builder while the form fieldset is being generated.
     * @return A Form
     * @since 5.1.0
     */
    @NonNull
    default <T> Form generate(@NonNull String action,
                              @NonNull Class<T> type,
                              @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        return generate(action, type, SUBMIT, builderConsumer);
    }

    /**
     *
     * @param action Form action attribute
     * @param method Form method attribute
     * @param type A class which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param submitValue input submit
     * @param <T> type
     * @param builderConsumer A BiConsumer with the property name and the builder. It allows to consume a form element builder while the form fieldset is being generated.
     * @return A Form
     * @since 5.1.0
     */
    @NonNull
    default <T> Form generate(@NonNull String action,
                              @NonNull String method,
                              @NonNull Class<T> type,
                              @NonNull Message submitValue,
                              @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        return generate(action, method, type, new InputSubmitFormElement(submitValue), builderConsumer);
    }

    /**
     * Generate a POST Form.
     * @param action Form action attribute
     * @param type A class which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param submitValue input submit
     * @param <T> type
     * @param builderConsumer A BiConsumer with the property name and the builder. It allows to consume a form element builder while the form fieldset is being generated.
     * @return A Form
     * @since 5.1.0
     */
    @NonNull
    default <T> Form generate(@NonNull String action,
                              @NonNull Class<T> type,
                              @NonNull Message submitValue,
                              @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        return generate(action, type, new InputSubmitFormElement(submitValue), builderConsumer);
    }

    /**
     *
     * @param action Form action attribute
     * @param method Form method attribute
     * @param type A class which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param inputSubmitFormElement input submit
     * @param <T> type
     * @param builderConsumer A BiConsumer with the property name and the builder. It allows to consume a form element builder while the form fieldset is being generated.
     * @return A Form
     * @since 5.1.0
     */
    @NonNull
    <T> Form generate(@NonNull String action,
                      @NonNull String method,
                      @NonNull Class<T> type,
                      @NonNull InputSubmitFormElement inputSubmitFormElement,
                      @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer);

    /**
     * Generate a POST form.
     * @param action Form action attribute
     * @param type A class which should be {@link io.micronaut.core.annotation.Introspected}.
     * @param inputSubmitFormElement input submit
     * @param <T> type
     * @param builderConsumer A BiConsumer with the property name and the builder. It allows to consume a form element builder while the form fieldset is being generated.
     * @return A Form
     * @since 5.1.0
     */
    @NonNull
    default <T> Form generate(@NonNull String action,
                              @NonNull Class<T> type,
                              @NonNull InputSubmitFormElement inputSubmitFormElement,
                              @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        return generate(action, POST, type, inputSubmitFormElement, builderConsumer);
    }
}

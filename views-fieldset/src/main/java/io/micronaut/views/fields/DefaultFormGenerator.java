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

import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.views.fields.elements.InputFileFormElement;
import io.micronaut.views.fields.elements.InputSubmitFormElement;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * {@link io.micronaut.context.annotation.DefaultImplementation} of {@link FormGenerator}.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Internal
@Singleton
public class DefaultFormGenerator implements FormGenerator {

    private final FieldsetGenerator fieldsetGenerator;

    /**
     *
     * @param fieldsetGenerator Fieldset Generator
     */
    public DefaultFormGenerator(FieldsetGenerator fieldsetGenerator) {
        this.fieldsetGenerator = fieldsetGenerator;
    }

    @Override
    public Form generateWithFieldset(@NonNull String action,
                                     @NonNull String method,
                                     @NonNull Fieldset fieldset,
                                     @NonNull InputSubmitFormElement inputSubmitFormElement) {
        return generate(action, method, fieldset, inputSubmitFormElement);
    }

    @Override
    public Form generate(@NonNull @NotBlank String action,
                         @NonNull @NotBlank String method,
                         @NonNull Object instance,
                         @NonNull @NotNull InputSubmitFormElement inputSubmitFormElement) {
        Fieldset fieldset = fieldsetGenerator.generate(instance);
        return generate(action, method, fieldset, inputSubmitFormElement);
    }

    @Override
    public Form generate(@NonNull @NotBlank String action,
                         @NonNull @NotBlank String method,
                         @NonNull Object instance,
                         @NonNull @NotNull InputSubmitFormElement inputSubmitFormElement,
                         @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        Fieldset fieldset = fieldsetGenerator.generate(instance, builderConsumer);
        return generate(action, method, fieldset, inputSubmitFormElement);
    }

    @Override
    public Form generate(@NonNull String action,
                         @NonNull String method,
                         @NonNull Object instance,
                         @NonNull ConstraintViolationException ex,
                         @NonNull InputSubmitFormElement inputSubmitFormElement) {
        Fieldset fieldset = fieldsetGenerator.generate(instance, ex);
        return generate(action, method, fieldset, inputSubmitFormElement);
    }

    @Override
    public Form generate(@NonNull @NotBlank String action,
                         @NonNull @NotBlank String method,
                         @NonNull Object instance,
                         @NonNull ConstraintViolationException ex,
                         @NonNull @NotNull InputSubmitFormElement inputSubmitFormElement,
                         @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        Fieldset fieldset = fieldsetGenerator.generate(instance, ex, builderConsumer);
        return generate(action, method, fieldset, inputSubmitFormElement);
    }

    @Override
    public <T> Form generate(@NonNull @NotBlank String action,
                             @NonNull @NotBlank String method,
                             @NonNull @NotNull Class<T> type,
                             @NonNull @NotNull InputSubmitFormElement inputSubmitFormElement) {
        Fieldset fieldset = fieldsetGenerator.generate(type);
        return generate(action, method, fieldset, inputSubmitFormElement);
    }

    @Override
    public <T> Form generate(@NonNull @NotBlank String action,
                             @NonNull @NotBlank String method,
                             @NonNull @NotNull Class<T> type,
                             @NonNull @NotNull InputSubmitFormElement inputSubmitFormElement,
                             @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        Fieldset fieldset = fieldsetGenerator.generate(type, builderConsumer);
        return generate(action, method, fieldset, inputSubmitFormElement);
    }

    private Form generate(@NonNull @NotBlank String action,
                          @NonNull @NotBlank String method,
                          @NonNull @NotNull Fieldset fieldset,
                          @NonNull @NotNull InputSubmitFormElement inputSubmitFormElement) {
        List<FormElement> fields = new ArrayList<>(fieldset.fields());
        fields.add(inputSubmitFormElement);
        return enctype(fieldset)
                .map(enctype -> new Form(action, method, new Fieldset(fields, fieldset.errors()), enctype))
                .orElseGet(() -> new Form(action, method, new Fieldset(fields, fieldset.errors())));
    }

    @NonNull
    private Optional<String> enctype(@NonNull Fieldset fieldset) {
        return fieldset.fields().stream().anyMatch(fe -> fe instanceof InputFileFormElement)
                ? Optional.of("multipart/form-data")
                : Optional.empty();
    }
}

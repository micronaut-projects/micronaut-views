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
package io.micronaut.views.fields.formelementresolvers;

import io.micronaut.core.beans.BeanProperty;
import io.micronaut.views.fields.FormElement;
import io.micronaut.views.fields.annotations.*;
import io.micronaut.views.fields.elements.*;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.Email;

import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;

/**
 * Default implementation of the {@link FormElementResolver} API.
 * @author Sergio del Amo
 * @since 5.1.0
 */
@Singleton
public class DefaultFormElementResolver implements FormElementResolver {
    public static final int ORDER = 0;
    private static final String CLASS_IO_MICRONAUT_DATA_ANNOTATION_AUTO_POPULATED = "io.micronaut.data.annotation.AutoPopulated";
    private static final Map<Class<? extends Annotation>, Class<? extends FormElement>> ANNOTATION_MAPPING = Map.ofEntries(
            Map.entry(InputHidden.class, InputHiddenFormElement.class),
            Map.entry(InputRadio.class, InputRadioFormElement.class),
            Map.entry(InputCheckbox.class, InputCheckboxFormElement.class),
            Map.entry(InputPassword.class, InputPasswordFormElement.class),
            Map.entry(InputEmail.class, InputEmailFormElement.class),
            Map.entry(Email.class, InputEmailFormElement.class),
            Map.entry(InputUrl.class, InputUrlFormElement.class),
            Map.entry(InputTel.class, InputTelFormElement.class),
            Map.entry(Select.class, SelectFormElement.class),
            Map.entry(Textarea.class, TextareaFormElement.class),
            Map.entry(TrixEditor.class, TrixEditorFormElement.class)
    );

    @Override
    public <B, T> Optional<Class<? extends FormElement>> resolve(BeanProperty<B, T> beanProperty) {
        if (beanProperty.hasStereotype(CLASS_IO_MICRONAUT_DATA_ANNOTATION_AUTO_POPULATED)) {
            return Optional.empty();
        }
        for (var mapping : ANNOTATION_MAPPING.entrySet()) {
            if (beanProperty.hasAnnotation(mapping.getKey())) {
                return Optional.of(mapping.getValue());
            }
        }
        if (beanProperty.getType() == LocalDate.class) {
            return Optional.of(InputDateFormElement.class);
        }
        if (beanProperty.getType() == LocalDateTime.class) {
            return Optional.of(InputDateTimeLocalFormElement.class);
        }
        if (beanProperty.getType() == LocalTime.class) {
            return Optional.of(InputTimeFormElement.class);
        }
        if (Number.class.isAssignableFrom(beanProperty.getType())) {
            return Optional.of(InputNumberFormElement.class);
        }
        if (beanProperty.getType() == boolean.class) {
            return Optional.of(InputCheckboxFormElement.class);
        }
        if (beanProperty.getType().isEnum()) {
            return Optional.of(SelectFormElement.class);
        }
        if (CharSequence.class.isAssignableFrom(beanProperty.getType())) {
            return Optional.of(InputTextFormElement.class);
        }

        return Optional.empty();
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}

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

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.views.fields.render.InputType;

import java.util.Collections;
import java.util.List;

/**
 * Input Telephone.
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/tel">Input Tel</a>
 * @param name Name of the form control. Submitted with the form as part of a name/value pair
 * @param id It defines an identifier (ID) which must be unique in the whole document
 * @param placeholder The placeholder attribute is a string that provides a brief hint to the user as to what kind of information is expected in the field.
 * @param required If true indicates that the user must specify a value for the input before the owning form can be submitted.
 * @param readOnly A Boolean attribute which, if present, means this field cannot be edited by the user.
 * @param maxLength The maximum string length that the user can enter into the text input.
 * @param minLength The minimum string length that the user can enter into the text input.
 * @param pattern The pattern attribute, when specified, is a regular expression that the input's value must match for the value to pass constraint validation.
 * @param size The size attribute is a numeric value indicating how many characters wide the input field should be.
 * @param value input tel value
 * @param label the input label
 * @param errors errors associated with this input
 *
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = InputTelFormElement.Builder.class))
public record InputTelFormElement(@NonNull String name,
                                  @Nullable String id,
                                  @Nullable String placeholder,
                                  boolean required,
                                  boolean readOnly,
                                  @Nullable Number maxLength,
                                  @Nullable Number minLength,
                                  @Nullable String pattern,
                                  @Nullable Integer size,
                                  @Nullable String value,
                                  @Nullable Message label,
                                  @NonNull List<Message> errors) implements InputFormElement, GlobalAttributes, FormElementAttributes, InputStringFormElement {

    @Override
    @NonNull
    public String getType() {
        return InputType.ATTR_TYPE_TEL;
    }

    /**
     *
     * @return the Input FormElement Builder
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Input Tel Builder.
     */
    public static class Builder extends InputStringFormBuilder<InputTelFormElement, Builder> {

        /**
         * @return Creates a {@link InputTelFormElement}.
         */
        @NonNull
        @Override
        public InputTelFormElement build() {
            return new InputTelFormElement(name, id, placeholder, required, readOnly, maxLength, minLength, pattern, size, value, label, errors == null ? Collections.emptyList() : errors);
        }
    }
}

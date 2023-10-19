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

import java.util.Collections;
import java.util.List;

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/url">Input URL</a>
 * @param name Name of the form control. Submitted with the form as part of a name/value pair
 * @param id It defines an identifier (ID) which must be unique in the whole document
 * @param placeholder The placeholder attribute is a string that provides a brief hint to the user as to what kind of information is expected in the field.
 * @param required If true indicates that the user must specify a value for the input before the owning form can be submitted.
 * @param readOnly A Boolean attribute which, if present, means this field cannot be edited by the user.
 * @param maxLength The maximum string length that the user can enter into the text input.
 * @param minLength The minimum string length that the user can enter into the text input.
 * @param pattern The pattern attribute, when specified, is a regular expression that the input's value must match for the value to pass constraint validation.
 * @param size The size attribute is a numeric value indicating how many characters wide the input field should be.
 * @param value input url value
 * @param label the input label
 * @param errors errors associated with this input
 */
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = InputUrlFormElement.Builder.class))
public record InputUrlFormElement(@NonNull String name,
                                  @NonNull String id,
                                  @Nullable String placeholder,
                                  boolean required,
                                  boolean readOnly,
                                  @Nullable Integer maxLength,
                                  @Nullable Integer minLength,
                                  @Nullable String pattern,
                                  @Nullable Integer size,
                                  @Nullable String value,
                                  @Nullable Message label,
                                  @NonNull List<Message> errors) implements FormElement, GlobalAttributes, FormElementAttributes, InputStringFormElement {

    /**
     *
     * @return the Input Url FormElement Builder
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Input URL Builder.
     */
    public static class Builder {

        private String name;

        private String id;

        private String placeholder;

        private boolean required;

        private boolean readOnly;

        private Integer maxLength;

        private Integer minLength;

        private Integer size;

        private String pattern;

        private String value;

        private List<Message> errors;

        private Message label;

        /**
         *
         * @param pattern The pattern attribute, when specified, is a regular expression that the input's value must match for the value to pass constraint validation.
         * @return The Builder
         */
        @NonNull
        public Builder pattern(@NonNull String pattern) {
            this.pattern = pattern;
            return this;
        }

        /**
         *
         * @param required If true indicates that the user must specify a value for the input before the owning form can be submitted.
         * @return The Builder
         */
        @NonNull
        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        /**
         *
         * @param readOnly A Boolean attribute which, if present, means this field cannot be edited by the user.
         * @return The Builder
         */
        @NonNull
        public Builder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        /**
         *
         * @param size The size attribute is a numeric value indicating how many characters wide the input field should be.
         * @return The Builder
         */
        @NonNull
        public Builder size(@NonNull Integer size) {
            this.size = size;
            return this;
        }

        /**
         *
         * @param maxLength The maximum string length that the user can enter into the text input.
         * @return The Builder
         */
        @NonNull
        public Builder maxLength(@NonNull Integer maxLength) {
            this.maxLength = maxLength;
            return this;
        }

        /**
         *
         * @param minLength The minimum string length that the user can enter into the text input.
         * @return The Builder
         */
        @NonNull
        public Builder minLength(@NonNull Integer minLength) {
            this.minLength = minLength;
            return this;
        }

        /**
         *
         * @param name Name of the form control. Submitted with the form as part of a name/value pair
         * @return The Builder
         */
        @NonNull
        public Builder name(@NonNull String name) {
            this.name = name;
            return this;
        }

        /**
         *
         * @param id It defines an identifier (ID) which must be unique in the whole document
         * @return The Builder
         */
        @NonNull
        public Builder id(@NonNull String id) {
            this.id = id;
            return this;
        }

        /**
         *
         * @param placeholder The placeholder attribute is a string that provides a brief hint to the user as to what kind of information is expected in the field.
         * @return the Builder
         */
        @NonNull
        public Builder placeholder(@NonNull String placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        /**
         *
         * @param value The value attribute of the input element
         * @return the Builder
         */
        @NonNull
        public Builder value(@NonNull String value) {
            this.value = value;
            return this;
        }

        /**
         *
         * @param label represents a caption for an item in a user interface
         * @return The Builder
         */
        @NonNull
        public Builder label(Message label) {
            this.label = label;
            return this;
        }

        /**
         *
         * @param errors Form element validation Errors.
         * @return The Builder
         */
        @NonNull
        public Builder errors(@NonNull List<Message> errors) {
            this.errors = errors;
            return this;
        }

        /**
         *
         * @return Creates a {@link InputUrlFormElement}.
         */
        @NonNull
        public InputUrlFormElement build() {
            return new InputUrlFormElement(name, id, placeholder, required, readOnly, maxLength, minLength, pattern, size, value, label, errors == null ? Collections.emptyList() : errors);
        }
    }
}

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

import java.util.Collection;

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/text">Input Text</a>
 */
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = InputTextFormElement.Builder.class))
public class InputTextFormElement extends FormElement {
    @NonNull
    private final String name;

    @Nullable
    private final String id;

    /**
     * The placeholder attribute is a string that provides a brief hint to the user as to what kind of information is expected in the field.
     */
    @Nullable
    private final String placeholder;

    private final boolean required;

    private final boolean readOnly;

    /**
     * The maximum string length that the user can enter into the text input.
     */
    @Nullable
    private Integer maxLength;

    /**
     * The minimum string length that the user can enter into the text input.
     */
    @Nullable
    private Integer minLength;

    /**
     * The size attribute is a numeric value indicating how many characters wide the input field should be.
     */
    @Nullable
    private Integer size;

    /**
     * The pattern attribute, when specified, is a regular expression that the input's value must match for the value to pass constraint validation.
     */
    @Nullable
    private final String pattern;

    /**
     *
     * @param name
     * @param id
     * @param placeholder The placeholder attribute is a string that provides a brief hint to the user as to what kind of information is expected in the field.
     * @param required
     * @param readOnly A Boolean attribute which, if present, means this field cannot be edited by the user.
     * @param maxLength The maximum string length that the user can enter into the text input.
     * @param minLength The minimum string length that the user can enter into the text input.
     * @param pattern The pattern attribute, when specified, is a regular expression that the input's value must match for the value to pass constraint validation.
     * @param size The size attribute is a numeric value indicating how many characters wide the input field should be.
     */
    public InputTextFormElement(@NonNull String name,
                                @NonNull String id,
                                @Nullable String placeholder,
                                boolean required,
                                boolean readOnly,
                                @Nullable Integer maxLength,
                                @Nullable Integer minLength,
                                @Nullable String pattern,
                                @Nullable Integer size) {
        this.name = name;
        this.id = id;
        this.placeholder = placeholder;
        this.required = required;
        this.readOnly = readOnly;
        this.maxLength = maxLength;
        this.minLength = minLength;
        this.pattern = pattern;
        this.size = size;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @Nullable
    public String getId() {
        return id;
    }

    /**
     *
     * @return The placeholder attribute is a string that provides a brief hint to the user as to what kind of information is expected in the field.
     */
    @Nullable
    public String getPlaceholder() {
        return placeholder;
    }

    /**
     *
     * @return A Boolean attribute which, if present, means this field cannot be edited by the user.
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isRequired() {
        return required;
    }

    /**
     *
     * @return The maximum string length that the user can enter into the text input.
     */
    @Nullable
    public Integer getMaxLength() {
        return maxLength;
    }

    /**
     *
     * @return The minimum string length that the user can enter into the text input.
     */
    @Nullable
    public Integer getMinLength() {
        return minLength;
    }

    /**
     *
     * @return The pattern attribute, when specified, is a regular expression that the input's value must match for the value to pass constraint validation.
     */
    @Nullable
    public String getPattern() {
        return pattern;
    }

    /**
     *
     * @return The size attribute is a numeric value indicating how many characters wide the input field should be.
     */
    public Integer getSize() {
        return size;
    }

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

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

        @NonNull
        public Builder pattern(@NonNull String pattern) {
            this.pattern = pattern;
            return this;
        }

        @NonNull
        public Builder required(boolean required) {
            this.required = required;
            return this;
        }


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

        @NonNull
        public Builder name(@NonNull String name) {
            this.name = name;
            return this;
        }

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

        @NonNull
        public InputTextFormElement build() {
            return new InputTextFormElement(name, id, placeholder, required, readOnly, maxLength, minLength, pattern, size);
        }
    }
}

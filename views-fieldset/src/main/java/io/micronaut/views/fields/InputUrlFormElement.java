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
import java.util.Objects;

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/text">Input Text</a>
 */
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = InputUrlFormElement.Builder.class))
public class InputUrlFormElement implements FormElement, GlobalAttributes, FormElementAttributes {
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
    private final Integer maxLength;

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

    @Nullable
    private final String value;

    @Nullable
    private final Message label;

    @NonNull
    private final List<Message> errors;

    /**
     *
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
    public InputUrlFormElement(@NonNull String name,
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
                               @NonNull List<Message> errors) {
        this.name = name;
        this.id = id;
        this.placeholder = placeholder;
        this.required = required;
        this.readOnly = readOnly;
        this.maxLength = maxLength;
        this.minLength = minLength;
        this.pattern = pattern;
        this.size = size;
        this.value = value;
        this.label = label;
        this.errors = errors;
    }

    @Override
    @NonNull
    public String getName() {
        return name;
    }

    @Override
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

    @Override
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

    /**
     *
     * @return Input url value.
     */
    @Nullable
    public String getValue() {
        return value;
    }

    @Override
    @Nullable
    public Message getLabel() {
        return label;
    }

    @Override
    @NonNull
    public List<Message> getErrors() {
        return errors;
    }

    @SuppressWarnings("NeedBraces")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InputUrlFormElement that)) return false;

        if (required != that.required) return false;
        if (readOnly != that.readOnly) return false;
        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(placeholder, that.placeholder))
            return false;
        if (!Objects.equals(maxLength, that.maxLength))
            return false;
        if (!Objects.equals(minLength, that.minLength))
            return false;
        if (!Objects.equals(size, that.size)) return false;
        if (!Objects.equals(pattern, that.pattern)) return false;
        if (!Objects.equals(value, that.value)) return false;
        if (!Objects.equals(label, that.label)) return false;
        return Objects.equals(errors, that.errors);
    }

    @SuppressWarnings("NeedBraces")
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (placeholder != null ? placeholder.hashCode() : 0);
        result = 31 * result + (required ? 1 : 0);
        result = 31 * result + (readOnly ? 1 : 0);
        result = 31 * result + (maxLength != null ? maxLength.hashCode() : 0);
        result = 31 * result + (minLength != null ? minLength.hashCode() : 0);
        result = 31 * result + (size != null ? size.hashCode() : 0);
        result = 31 * result + (pattern != null ? pattern.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (errors != null ? errors.hashCode() : 0);
        return result;
    }

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

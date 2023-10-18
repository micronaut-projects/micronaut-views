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
 * Input type Number.
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/number">input number</a>
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = InputNumberFormElement.Builder.class))
public class InputNumberFormElement implements FormElement, GlobalAttributes, FormElementAttributes {
    @NonNull
    private final String name;

    @Nullable
    private final String id;

    @Nullable
    private final Number value;

    /**
     * The maximum value to accept for this input.
     */
    @Nullable
    private final Number max;

    /**
     * The minimum value to accept for this input.
     */
    @Nullable
    private final Number min;

    /**
     * The placeholder attribute is a string that provides a brief hint to the user as to what kind of information is expected in the field.
     */
    @Nullable
    private final String placeholder;

    private final boolean required;

    /**
     * A Boolean attribute which, if present, means this field cannot be edited by the user.
     */
    private final boolean readOnly;


    /**
     * The step attribute is a number that specifies the granularity that the value must adhere to, or the special value any.
     */
    @Nullable
    private final String step;

    @NonNull
    private final Message label;

    @NonNull
    private final List<Message> errors;

    /**
     *
     * @param name Name of the form control. Submitted with the form as part of a name/value pair
     * @param id It defines an identifier (ID) which must be unique in the whole document
     * @param value A number representing the value of the input number.
     * @param max The maximum value to accept for this input.
     * @param min The minimum value to accept for this input.
     * @param placeholder The placeholder attribute is a string that provides a brief hint to the user as to what kind of information is expected in the field.
     * @param required If true indicates that the user must specify a value for the input before the owning form can be submitted.
     * @param readOnly A Boolean attribute which, if present, means this field cannot be edited by the user.
     * @param step The step attribute is a number that specifies the granularity that the value must adhere to, or the special value any.
     * @param label represents a caption for an item in a user interface
     * @param errors errors associated with this input
     */
    public InputNumberFormElement(@NonNull String name,
                                  @Nullable String id,
                                  @Nullable Number value,
                                  @Nullable Number max,
                                  @Nullable Number min,
                                  @Nullable String placeholder,
                                  boolean required,
                                  boolean readOnly,
                                  @Nullable String step,
                                  @NonNull Message label,
                                  @NonNull List<Message> errors) {
        this.name = name;
        this.id = id;
        this.value = value;
        this.max = max;
        this.min = min;
        this.placeholder = placeholder;
        this.required = required;
        this.readOnly = readOnly;
        this.step = step;
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
     * @return A number representing the value of the input number
     */
    @Nullable
    public Number getValue() {
        return value;
    }

    /**
     *
     * @return The maximum value to accept for this input.
     */
    @Nullable
    public Number getMax() {
        return max;
    }

    /**
     *
     * @return The minimum value to accept for this input.
     */
    @Nullable
    public Number getMin() {
        return min;
    }

    /**
     *
     * @return The placeholder attribute is a string that provides a brief hint to the user as to what kind of information is expected in the field.
     */
    @Nullable
    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    /**
     *
     * @return A Boolean attribute which, if present, means this field cannot be edited by the user.
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     *
     * @return The step attribute is a number that specifies the granularity that the value must adhere to, or the special value any.
     */
    @Nullable
    public String getStep() {
        return step;
    }

    @Override
    @NonNull
    public Message getLabel() {
        return label;
    }

    @Override
    @NonNull
    public List<Message> getErrors() {
        return this.errors;
    }

    @SuppressWarnings("NeedBraces")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InputNumberFormElement that)) return false;

        if (required != that.required) return false;
        if (readOnly != that.readOnly) return false;
        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(value, that.value)) return false;
        if (!Objects.equals(max, that.max)) return false;
        if (!Objects.equals(min, that.min)) return false;
        if (!Objects.equals(placeholder, that.placeholder))
            return false;
        if (!Objects.equals(step, that.step)) return false;
        if (!Objects.equals(label, that.label)) return false;
        return Objects.equals(errors, that.errors);
    }

    @SuppressWarnings("NeedBraces")
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (max != null ? max.hashCode() : 0);
        result = 31 * result + (min != null ? min.hashCode() : 0);
        result = 31 * result + (placeholder != null ? placeholder.hashCode() : 0);
        result = 31 * result + (required ? 1 : 0);
        result = 31 * result + (readOnly ? 1 : 0);
        result = 31 * result + (step != null ? step.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (errors != null ? errors.hashCode() : 0);
        return result;
    }


    /**
     *
     * @return the Input Number FormElement Builder
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Input Number Builder.
     */
    public static class Builder {

        @NonNull
        private String name;

        @Nullable
        private String id;

        @Nullable
        private Number value;

        @Nullable
        private Number max;

        @Nullable
        private Integer min;

        @Nullable
        private String placeholder;

        @Nullable
        private String step;

        private boolean required;

        private boolean readOnly;

        private Message label;

        private List<Message> errors;

        /**
         *
         * @param required If true indicates that the user must specify a value for the input before the owning form can be submitted.
         * @return the Builder
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
         * @return the Builder
         */
        @NonNull
        public Builder id(@Nullable String id) {
            this.id = id;
            return this;
        }

        /**
         *
         * @param value A number representing the value of the input number
         * @return a Builder
         */
        @NonNull
        public Builder value(@Nullable Number value) {
            this.value = value;
            return this;
        }

        /**
         *
         * @param max The maximum value to accept for this input.
         * @return The Builder
         */
        @NonNull
        public Builder max(@Nullable Number max) {
            this.max = max;
            return this;
        }

        /**
         *
         * @param min The minimum value to accept for this input.
         * @return The Builder
         */
        @NonNull
        public Builder min(@Nullable Integer min) {
            this.min = min;
            return this;
        }

        /**
         *
         * @param placeholder The placeholder attribute is a string that provides a brief hint to the user as to what kind of information is expected in the field.
         * @return The Builder
         */
        @NonNull
        public Builder placeholder(@Nullable String placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        /**
         *
         * @param step The step attribute is a number that specifies the granularity that the value must adhere to, or the special value any.
         * @return The builder
         */
        @NonNull
        public Builder step(@Nullable String step) {
            this.step = step;
            return this;
        }

        /**
         *
         * @param label represents a caption for an item in a user interface
         * @return the Builder
         */
        @NonNull
        public Builder label(Message label) {
            this.label = label;
            return this;
        }

        /**
         *
         * @param errors Form element validation Errors.
         * @return the Builder
         */
        @NonNull
        public Builder errors(@NonNull List<Message> errors) {
            this.errors = errors;
            return this;
        }

        /**
         *
         * @return Instantiate a {@link InputNumberFormElement}.
         */
        @NonNull
        public InputNumberFormElement build() {
            return new InputNumberFormElement(name, id, value, max, min, placeholder, required, readOnly, step, label, errors == null ? Collections.emptyList() : errors);
        }

    }
}

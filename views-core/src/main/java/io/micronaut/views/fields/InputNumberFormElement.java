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

import java.util.Objects;

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/number">input number</a>
 */
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = InputNumberFormElement.Builder.class))
public class InputNumberFormElement extends FormElement {
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
    private final Integer max;

    /**
     * The minimum value to accept for this input.
     */
    @Nullable
    private final Integer min;

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

    public InputNumberFormElement(@NonNull String name,
                                  @Nullable String id,
                                  @Nullable Number value,
                                  @Nullable Integer max,
                                  @Nullable Integer min,
                                  @Nullable String placeholder,
                                  boolean required,
                                  boolean readOnly,
                                  @Nullable String step,
                                  @NonNull Message label) {
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
    }

    @NonNull
    public String getName() {
        return name;
    }

    @Nullable
    public String getId() {
        return id;
    }

    @Nullable
    public Number getValue() {
        return value;
    }

    @Nullable
    public Integer getMax() {
        return max;
    }

    @Nullable
    public Integer getMin() {
        return min;
    }

    @Nullable
    public String getPlaceholder() {
        return placeholder;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    @Nullable
    public String getStep() {
        return step;
    }

    @NonNull
    public Message getLabel() {
        return label;
    }

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
        return Objects.equals(label, that.label);
    }

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
        return result;
    }

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        @NonNull
        private String name;

        @Nullable
        private String id;

        @Nullable
        private Number value;

        @Nullable
        private Integer max;

        @Nullable
        private Integer min;

        @Nullable
        private String placeholder;

        @Nullable
        String step;

        private boolean required;

        private boolean readOnly;

        private Message label;

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

        @NonNull
        public Builder name(@NonNull String name) {
            this.name = name;
            return this;
        }

        @NonNull
        public Builder id(@Nullable String id) {
            this.id = id;
            return this;
        }

        @NonNull
        public Builder value(@Nullable Number value) {
            this.value = value;
            return this;
        }

        @NonNull
        public Builder max(@Nullable Integer max) {
            this.max = max;
            return this;
        }

        @NonNull
        public Builder min(@Nullable Integer min) {
            this.min = min;
            return this;
        }

        @NonNull
        public Builder placeholder(@Nullable String placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        @NonNull
        public Builder step(@Nullable String step) {
            this.step = step;
            return this;
        }


        @NonNull
        public Builder label(Message label) {
            this.label = label;
            return this;
        }

        @NonNull
        public InputNumberFormElement build() {
            return new InputNumberFormElement(name, id, value, max, min, placeholder, required, readOnly, step, label);
        }

    }
}

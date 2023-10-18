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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Input Date.
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/date">Input date</a>
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = InputDateFormElement.Builder.class))
public class InputDateFormElement implements FormElement, GlobalAttributes, FormElementAttributes {
    @NonNull
    private final String name;

    @Nullable
    private final String id;

    @Nullable
    private final LocalDate min;

    @Nullable
    private final LocalDate max;

    private final boolean required;

    @Nullable
    private final LocalDate value;

    @Nullable
    private final Message label;

    @NonNull
    private final List<Message> errors;

    /**
     *
     * @param name Name of the form control. Submitted with the form as part of a name/value pair
     * @param id It defines an identifier (ID) which must be unique in the whole document
     * @param required If true indicates that the user must specify a value for the input before the owning form can be submitted.
     * @param max The latest date to accept
     * @param min The earliest date to accept.
     * @param value the input date value
     * @param label represents a caption for an item in a user interface
     * @param errors Form element validation Errors.
     */
    public InputDateFormElement(@NonNull String name,
                                     @NonNull String id,
                                     boolean required,
                                     @Nullable LocalDate max,
                                     @Nullable LocalDate min,
                                     @Nullable LocalDate value,
                                     @Nullable Message label,
                                     @NonNull List<Message> errors) {
        this.name = name;
        this.id = id;
        this.required = required;
        this.max = max;
        this.min = min;
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

    @Override
    public boolean isRequired() {
        return required;
    }

    /**
     *
     * @return The latest date to accept
     */
    @Nullable
    public LocalDate getMax() {
        return max;
    }

    /**
     *
     * @return The earliest date to accept.
     */
    @Nullable
    public LocalDate getMin() {
        return min;
    }

    /**
     *
     * @return the input date value
     */
    @Nullable
    public LocalDate getValue() {
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
        if (!(o instanceof InputDateFormElement that)) return false;

        if (required != that.required) return false;
        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(min, that.min)) return false;
        if (!Objects.equals(max, that.max)) return false;
        if (!Objects.equals(value, that.value)) return false;
        if (!Objects.equals(label, that.label)) return false;
        return Objects.equals(errors, that.errors);
    }

    @SuppressWarnings("NeedBraces")
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (min != null ? min.hashCode() : 0);
        result = 31 * result + (max != null ? max.hashCode() : 0);
        result = 31 * result + (required ? 1 : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (errors != null ? errors.hashCode() : 0);
        return result;
    }

    /**
     *
     * @return the Input Date FormElement Builder
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Input Date Builder.
     */
    public static class Builder {

        private String name;

        private String id;

        private boolean required;

        private LocalDate max;

        private LocalDate min;

        private LocalDate value;

        private List<Message> errors;

        private Message label;

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
         * @param max The latest date to accep
         * @return The Builder
         */
        @NonNull
        public Builder max(@NonNull LocalDate max) {
            this.max = max;
            return this;
        }

        /**
         *
         * @param min The earliest date to accept.
         * @return the Builder
         */
        @NonNull
        public Builder min(@NonNull LocalDate min) {
            this.min = min;
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
         * @param value The value attribute of the input element
         * @return the Builder
         */
        @NonNull
        public Builder value(@NonNull LocalDate value) {
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
         * @return Creates a {@link InputDateFormElement}.
         */
        @NonNull
        public InputDateFormElement build() {
            return new InputDateFormElement(name,
                id,
                required,
                max,
                min,
                value,
                label,
                errors == null ? Collections.emptyList() : errors);
        }
    }
}

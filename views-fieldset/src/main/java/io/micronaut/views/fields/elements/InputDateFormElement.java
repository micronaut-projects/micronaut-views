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
package io.micronaut.views.fields.elements;

import io.micronaut.core.annotation.Experimental;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.views.fields.InputType;
import io.micronaut.views.fields.messages.Message;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Input Date.
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/date">Input date</a>
 * @param name Name of the form control. Submitted with the form as part of a name/value pair
 * @param id It defines an identifier (ID) which must be unique in the whole document
 * @param required If true indicates that the user must specify a value for the input before the owning form can be submitted.
 * @param max The latest date to accept
 * @param min The earliest date to accept.
 * @param value the input date value
 * @param label represents a caption for an item in a user interface
 * @param step Granularity that the value must adhere to
 * @param errors Form element validation Errors.
 *
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Experimental
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = InputDateFormElement.Builder.class))
public record InputDateFormElement(@NonNull String name,
                                   @Nullable String id,
                                   boolean required,
                                   @Nullable LocalDate max,
                                   @Nullable LocalDate min,
                                   @Nullable LocalDate value,

                                   @Nullable String step,
                                   @Nullable Message label,
                                   @NonNull List<Message> errors) implements InputFormElement, GlobalAttributes, FormElementAttributes {
    /**
     *
     * @return the Input Date FormElement Builder
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    @Override
    @NonNull
    public String getType() {
        return InputType.ATTR_TYPE_DATE;
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
        private String step;
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
         * @param max The latest date to accept
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
         * @param step Granularity that the value must adhere to
         * @return The Builder
         */
        @NonNull
        public Builder step(@NonNull String step) {
            this.step = step;
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
                step,
                label,
                errors == null ? Collections.emptyList() : errors);
        }
    }
}

/*
 * Copyright 2017-2024 original authors
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
import io.micronaut.views.fields.FormElement;
import io.micronaut.views.fields.messages.Message;

/**
 * A Checkbox Form Element.
 *
 * @param name     Name of the form control. Submitted with the form as part of a name/value pair
 * @param value    A string representing the value of the checkbox.
 * @param checked  A boolean attribute indicating whether this checkbox is checked by default (when the page loads).
 * @param disabled A boolean attribute indicating whether this checkbox is disabled by default (when the page loads).
 * @param required If true indicates that the user must specify a value for the input before the owning form can be submitted.
 * @param id       It defines an identifier (ID) which must be unique in the whole document
 * @param label    represents a caption for an item in a user interface
 *
 * @author Sergio del Amo
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/checkbox">Input Checkbox</a>
 * @since 4.1.0
 */
@Experimental
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = Checkbox.Builder.class))
public record Checkbox(@NonNull String name,
                       @NonNull String value,
                       boolean checked,
                       boolean disabled,
                       boolean required,
                       @Nullable String id,
                       @Nullable Message label) implements FormElement, GlobalAttributes {

    /**
     * Instantiates a Checkbox.
     *
     * @param name     Name of the form control. Submitted with the form as part of a name/value pair
     * @param value    A string representing the value of the checkbox.
     * @param checked  A boolean attribute indicating whether this checkbox is checked by default (when the page loads).
     * @param required If true indicates that the user must specify a value for the input before the owning form can be submitted.
     * @param id       It defines an identifier (ID) which must be unique in the whole document
     * @param label    represents a caption for an item in a user interface
     * @deprecated Use {@link Checkbox(String, String, boolean, boolean, boolean, String, Message)} instead which includes disabled attribute.
     */
    @Deprecated(forRemoval = true, since = "5.2.0")
    public Checkbox(
        @NonNull String name,
        @NonNull String value,
        boolean checked,
        boolean required,
        @Nullable String id,
        @Nullable Message label
    ) {
        this(name, value, checked, false, required, id, label);
    }

    /**
     * @return A checkbox builder.
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Checkbox Builder.
     */
    public static final class Builder {

        private String name;
        private String id;
        private boolean checked;
        private boolean disabled;
        private boolean required;
        private String value;
        private Message label;

        /**
         * @param name Name of the form control. Submitted with the form as part of a name/value pair
         * @return The Checkbox Builder
         */
        @NonNull
        public Builder name(@NonNull String name) {
            this.name = name;
            return this;
        }

        /**
         * @param id It defines an identifier (ID) which must be unique in the whole document
         * @return The Checkbox Builder
         */
        @NonNull
        public Builder id(@NonNull String id) {
            this.id = id;
            return this;
        }

        /**
         * @param value A string representing the value of the checkbox.
         * @return the Builder
         */
        @NonNull
        public Builder value(@NonNull String value) {
            this.value = value;
            return this;
        }

        /**
         * @param label represents a caption for an item in a user interface
         * @return The Checkbox Builder
         */
        @NonNull
        public Builder label(Message label) {
            this.label = label;
            return this;
        }

        /**
         * @param checked A boolean attribute indicating whether this checkbox is checked by default (when the page loads).
         * @return The Checkbox Builder
         */
        @NonNull
        public Builder checked(boolean checked) {
            this.checked = checked;
            return this;
        }

        /**
         * @param disabled A boolean attribute indicating whether this checkbox is disabled by default (when the page loads).
         * @return The Checkbox Builder
         */
        @NonNull
        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        /**
         * @param required If true indicates that the user must specify a value for the input before the owning form can be submitted.
         * @return The Checkbox Builder
         */
        @NonNull
        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        /**
         * Instantiates a Checkbox.
         *
         * @return A Checkbox
         */
        @NonNull
        public Checkbox build() {
            return new Checkbox(name,
                value,
                checked,
                disabled,
                required,
                id,
                label);
        }
    }
}

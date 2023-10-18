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
 * A Checkbox Form Element.
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/checkbox">Input Checkbox</a>
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = Checkbox.Builder.class))
public class Checkbox implements FormElement, GlobalAttributes, FormElementAttributes {

    @NonNull
    private final String name;

    @NonNull
    private final String value;

    private final boolean checked;

    private final boolean required;

    @Nullable
    private final String id;

    @Nullable
    private final Message label;

    @NonNull
    private final List<Message> errors;

    /**
     *
     * @param name Name of the form control. Submitted with the form as part of a name/value pair
     * @param value A string representing the value of the checkbox.
     * @param checked A boolean attribute indicating whether this checkbox is checked by default (when the page loads).
     * @param required If true indicates that the user must specify a value for the input before the owning form can be submitted.
     * @param id It defines an identifier (ID) which must be unique in the whole document
     * @param label represents a caption for an item in a user interface
     * @param errors Form element validation Errors.
     */
    public Checkbox(@NonNull String name,
                    String value,
                    boolean checked,
                    boolean required,
                    @Nullable String id,
                    @Nullable Message label,
                    @NonNull List<Message> errors) {
        this.name = name;
        this.value = value;
        this.checked = checked;
        this.required = required;
        this.id = id;
        this.label = label;
        this.errors = errors;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    @NonNull
    public String getName() {
        return name;
    }

    /**
     *
     * @return A string representing the value of the checkbox.
     */
    @NonNull
    public String getValue() {
        return value;
    }

    /**
     *
     * @return A boolean attribute indicating whether this checkbox is checked by default (when the page loads).
     */
    public boolean isChecked() {
        return checked;
    }

    @Override
    @Nullable
    public String getId() {
        return id;
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
        if (!(o instanceof Checkbox checkbox)) return false;

        if (checked != checkbox.checked) return false;
        if (required != checkbox.required) return false;
        if (!Objects.equals(name, checkbox.name)) return false;
        if (!Objects.equals(value, checkbox.value)) return false;
        if (!Objects.equals(id, checkbox.id)) return false;
        if (!Objects.equals(label, checkbox.label)) return false;
        return Objects.equals(errors, checkbox.errors);
    }

    @SuppressWarnings("NeedBraces")
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (checked ? 1 : 0);
        result = 31 * result + (required ? 1 : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (errors != null ? errors.hashCode() : 0);
        return result;
    }

    /**
     *
     * @return A checkbox builder.
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Checkbox Builder.
     */
    public static class Builder {

        private String name;

        private String id;

        private boolean checked;

        private boolean required;

        private String value;

        private List<Message> errors;

        private Message label;

        /**
         *
         * @param name Name of the form control. Submitted with the form as part of a name/value pair
         * @return The Checkbox Builder
         */
        @NonNull
        public Builder name(@NonNull String name) {
            this.name = name;
            return this;
        }

        /**
         *
         * @param id It defines an identifier (ID) which must be unique in the whole document
         * @return The Checkbox Builder
         */
        @NonNull
        public Builder id(@NonNull String id) {
            this.id = id;
            return this;
        }

        /**
         *
         * @param value A string representing the value of the checkbox.
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
         * @return The Checkbox Builder
         */
        @NonNull
        public Builder label(Message label) {
            this.label = label;
            return this;
        }

        /**
         *
         * @param errors Form element validation Errors.
         * @return The Checkbox Builder
         */
        @NonNull
        public Builder errors(@NonNull List<Message> errors) {
            this.errors = errors;
            return this;
        }

        /**
         *
         * @param checked A boolean attribute indicating whether this checkbox is checked by default (when the page loads).
         * @return The Checkbox Builder
         */
        @NonNull
        public Builder checked(boolean checked) {
            this.checked = checked;
            return this;
        }

        /**
         *
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
         * @return A Checkbox
         */
        @NonNull
        public Checkbox build() {
            return new Checkbox(name,
                value,
                checked,
                required,
                id,
                label,
                errors == null ? Collections.emptyList() : errors);
        }
    }
}

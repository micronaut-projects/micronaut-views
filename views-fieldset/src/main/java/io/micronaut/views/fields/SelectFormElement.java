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
 * HTML Select.
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/select">Select</a>
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = SelectFormElement.Builder.class))
public class SelectFormElement implements FormElement, GlobalAttributes {
    private final boolean required;
    @NonNull
    private final String name;

    @Nullable
    private final String id;

    @NonNull
    private final List<Option> options;

    @NonNull
    private final Message label;

    /**
     *
     * @param required If true indicates that the user must specify a value for the input before the owning form can be submitted.
     * @param name Name of the form control. Submitted with the form as part of a name/value pair
     * @param id It defines an identifier (ID) which must be unique in the whole document
     * @param options Select Options
     * @param label represents a caption for an item in a user interface
     */
    public SelectFormElement(boolean required,
                             @NonNull String name,
                             @Nullable String id,
                             @NonNull List<Option> options,
                             Message label) {
        this.required = required;
        this.name = name;
        this.id = id;
        this.options = options;
        this.label = label;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    /**
     *
     * @return represents a caption for an item in a user interface
     */
    @NonNull
    public Message getLabel() {
        return label;
    }

    /**
     *
     * @return The name of the control.
     */
    @NonNull
    public String getName() {
        return this.name;
    }

    /**
     *
     * @return An id attribute. Often used to allow the form element to be associated with a label element for accessibility purposes.
     */
    @Override
    @Nullable
    public String getId() {
        return this.id;
    }

    /**
     *
     * @return Select Options
     */
    @NonNull
    public List<Option> getOptions() {
        return options;
    }

    @SuppressWarnings("NeedBraces")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SelectFormElement that)) return false;

        if (required != that.required) return false;
        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(options, that.options)) return false;
        return Objects.equals(label, that.label);
    }

    @SuppressWarnings("NeedBraces")
    @Override
    public int hashCode() {
        int result = (required ? 1 : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (options != null ? options.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        return result;
    }

    /**
     *
     * @return A Select Builder
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }


    /**
     * Select Builder.
     */
    public static class Builder {

        private boolean required;

        @NonNull
        private String name;

        @Nullable
        private String id;

        private List<Option> options;

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
        public Builder id(@Nullable String id) {
            this.id = id;
            return this;
        }

        /**
         *
         * @param options select options
         * @return the Builder
         */
        @NonNull
        public Builder options(List<Option> options) {
            this.options = options;
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
         * Creates a Select form element.
         * @return A Select
         */
        @NonNull
        public SelectFormElement build() {
            return new SelectFormElement(
                required,
                Objects.requireNonNull(name),
                id,
                options == null ? Collections.emptyList() : options,
                label);
        }
    }
}

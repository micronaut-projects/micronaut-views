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

@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = SelectFormElement.Builder.class))
public class SelectFormElement extends FormElement {
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
     * @param name The name attribute.
     * @param id The id attribute.
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

    public boolean isRequired() {
        return required;
    }

    @NonNull
    public Message getLabel() {
        return label;
    }

    /*
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
    @Nullable
    public String getId() {
        return this.id;
    }

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    @NonNull
    public List<Option> getOptions() {
        return options;
    }

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

    @Override
    public int hashCode() {
        int result = (required ? 1 : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (options != null ? options.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        return result;
    }

    public static class Builder {

        private boolean required;

        @NonNull
        private String name;

        @Nullable
        private String id;

        private List<Option> options;

        private Message label;

        @NonNull
        public Builder required(boolean required) {
            this.required = required;
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
        public Builder options(List<Option> options) {
            this.options = options;
            return this;
        }

        @NonNull
        public Builder label(Message label) {
            this.label = label;
            return this;
        }

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

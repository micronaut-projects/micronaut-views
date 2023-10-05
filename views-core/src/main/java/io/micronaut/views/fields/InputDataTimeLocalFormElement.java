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

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/datatime-local">Input datetime-local</a>
 */
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = InputDataTimeLocalFormElement.Builder.class))
public class InputDataTimeLocalFormElement extends FormElement {
    @NonNull
    private final String name;

    @Nullable
    private final String id;

    @Nullable
    private final LocalDateTime min;

    @Nullable
    private final LocalDateTime max;

    private final boolean required;

    @Nullable
    private final LocalDateTime value;

    @Nullable
    private final Message label;

    @NonNull
    private final Collection<Message> errors;

    public InputDataTimeLocalFormElement(@NonNull String name,
                                         @NonNull String id,
                                         boolean required,
                                         @Nullable LocalDateTime max,
                                         @Nullable LocalDateTime min,
                                         @Nullable LocalDateTime value,
                                         @Nullable Message label,
                                         @NonNull Collection<Message> errors) {
        this.name = name;
        this.id = id;
        this.required = required;
        this.max = max;
        this.min = min;
        this.value = value;
        this.label = label;
        this.errors = errors;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @Nullable
    public String getId() {
        return id;
    }

    public boolean isRequired() {
        return required;
    }

    @Nullable
    public LocalDateTime getMax() {
        return max;
    }

    @Nullable
    public LocalDateTime getMin() {
        return min;
    }

    @Nullable
    public LocalDateTime getValue() {
        return value;
    }

    @Nullable
    public Message getLabel() {
        return label;
    }

    @NonNull
    public Collection<Message> getErrors() {
        return errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InputDataTimeLocalFormElement that)) return false;

        if (required != that.required) return false;
        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(min, that.min)) return false;
        if (!Objects.equals(max, that.max)) return false;
        if (!Objects.equals(value, that.value)) return false;
        if (!Objects.equals(label, that.label)) return false;
        return Objects.equals(errors, that.errors);
    }

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

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String name;

        private String id;

        private boolean required;

        private LocalDateTime max;

        private LocalDateTime min;

        private LocalDateTime value;

        private List<Message> errors;

        private Message label;

        @NonNull
        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        @NonNull
        public Builder max(@NonNull LocalDateTime max) {
            this.max = max;
            return this;
        }

        @NonNull
        public Builder min(@NonNull LocalDateTime min) {
            this.min = min;
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
         * @param value The value attribute of the input element
         * @return the Builder
         */
        @NonNull
        public Builder value(@NonNull LocalDateTime value) {
            this.value = value;
            return this;
        }

        @NonNull
        public Builder label(Message label) {
            this.label = label;
            return this;
        }

        @NonNull
        public Builder errors(@NonNull List<Message> errors) {
            this.errors = errors;
            return this;
        }

        @NonNull
        public InputDataTimeLocalFormElement build() {
            return new InputDataTimeLocalFormElement(name,
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

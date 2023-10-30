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
import io.micronaut.views.fields.render.InputType;

import java.util.Collections;
import java.util.List;

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/checkbox">Input Checkbox</a>
 * @param checkboxes Checkboxes
 * @param label HTML label
 * @param errors errors associated with this input
 *
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = InputCheckboxFormElement.Builder.class))
public record InputCheckboxFormElement(@NonNull List<Checkbox> checkboxes,
                                       @NonNull Message label,
                                       @NonNull List<Message> errors) implements InputFormElement {
    @Override
    @NonNull
    public String getType() {
        return InputType.ATTR_TYPE_CHECKBOX;
    }

    /**
     *
     * @return Whether the form element has validation errors
     */
    public boolean hasErrors() {
        return !errors().isEmpty();
    }

    /**
     *
     * @return Creates a {@link InputCheckboxFormElement} Builder.
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Input checkbox builder.
     */
    public static class Builder {
        @Nullable
        private List<Checkbox> checkboxes;

        private Message label;

        private List<Message> errors;

        /**
         *
         * @param checkboxes Checkboxes
         * @return The Builder
         */
        @NonNull
        public Builder checkboxes(List<Checkbox> checkboxes) {
            this.checkboxes = checkboxes;
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
         * @return Creates a {@link InputCheckboxFormElement}
         */
        @NonNull
        public InputCheckboxFormElement build() {
            return new InputCheckboxFormElement(checkboxes == null ? Collections.emptyList() : checkboxes,
                label,
                errors == null ? Collections.emptyList() : errors);
        }
    }
}

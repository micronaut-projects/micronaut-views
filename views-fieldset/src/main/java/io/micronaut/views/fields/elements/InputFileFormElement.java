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

import java.util.Collections;
import java.util.List;

/**
 * Input File.
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/file">Input File</a>
 * @param name Name of the form control. Submitted with the form as part of a name/value pair
 * @param id It defines an identifier (ID) which must be unique in the whole document
 * @param accept It defines the file types the file input should accept.
 * @param required If true indicates that the user must specify a value for the input before the owning form can be submitted.
 * @param label the input label
 * @param errors errors associated with this input
 *
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Experimental
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = InputFileFormElement.Builder.class))
public record InputFileFormElement(@NonNull String name,
                                   @Nullable String id,
                                   @Nullable String accept,
                                   boolean required,
                                   @Nullable Message label,
                                   @NonNull List<Message> errors) implements InputFormElement, GlobalAttributes, FormElementAttributes {
    @Override
    @NonNull
    public InputType getType() {
        return InputType.FILE;
    }

    /**
     *
     * @return Input Text builder
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Input Text Builder.
     */
    public static final class Builder {
        /**
         *
         * Name of the form control. Submitted with the form as part of a name/value pair.
         */
        private String name;

        /**
         *
         * It defines an identifier (ID) which must be unique in the whole document.
         */
        private String id;

        /**
         *
         * The accept attribute defines the file types the file input should accept.
         */
        private String accept;

        /**
         *
         * If true indicates that the user must specify a value for the input before the owning form can be submitted.
         */
        private boolean required;

        /**
         *
         * Input Errors.
         */
        private List<Message> errors;

        /**
         *
         * message for an HTML Label element.
         */
        private Message label;

        /**
         *
         * @param accept The accept attribute defines the file types the file input should accept.
         * @return The Builder
         */
        @NonNull
        public Builder accept(@NonNull String accept) {
            this.accept = accept;
            return this;
        }

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
         * @param name Name of the form control. Submitted with the form as part of a name/value pair
         * @return the Builder
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
         * @return The builder
         */
        @NonNull
        public Builder errors(@NonNull List<Message> errors) {
            this.errors = errors;
            return this;
        }

        /**

        /**
         *
         * @return Creates a {@link InputFileFormElement}.
         */
        @NonNull
        public InputFileFormElement build() {
            return new InputFileFormElement(name, id, accept, required, label, errors == null ? Collections.emptyList() : errors);
        }
    }
}

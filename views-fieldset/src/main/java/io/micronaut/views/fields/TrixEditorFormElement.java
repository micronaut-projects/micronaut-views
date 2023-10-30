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

/**
 * Trix editor form element.
 * @see <a href="https://trix-editor.org">Trix Editor</a>
 * @param name Name of the form control. Submitted with the form as part of a name/value pair
 * @param id It defines an identifier (ID) which must be unique in the whole document
 * @param value HTML to be loaded in the editor
 * @param label represents a caption for an item in a user interface
 * @param errors Form element validation Errors.
 *
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = TrixEditorFormElement.Builder.class))
public record TrixEditorFormElement(@NonNull String name,
                                    @Nullable String id,
                                    @Nullable String value,
                                    @Nullable Message label,
                                    @NonNull List<Message> errors) implements FormElement, FormElementAttributes {

    @Override
    public String getTag() {
        return HtmlTag.TRIX_EDITOR;
    }

    /**
     *
     * @return Trix Editor  FormElement Builder
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Textarea form builder.
     */
    public static class Builder {

        private String name;
        private String id;
        private String value;
        private List<Message> errors;
        private Message label;

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
         * @param value html to be loaded in the editor
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
         * @return Creates a Trix Editor FormElement.
         */
        @NonNull
        public TrixEditorFormElement build() {
            return new TrixEditorFormElement(name,
                id,
                value,
                label,
                errors != null ? errors : Collections.emptyList());
        }
    }
}

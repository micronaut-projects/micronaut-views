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

import io.micronaut.core.annotation.Experimental;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

/**
 * A Radio Form Element.
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/radio">Input Radio</a>
 * @param value the value of the input radio element
 * @param id It defines an identifier (ID) which must be unique in the whole document
 * @param label represents a caption for an item in a user interface
 * @param checked whether the radio button is checked
 *
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Experimental
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = Radio.Builder.class))
public record Radio(@NonNull String value,
                    @Nullable String id,
                    @NonNull Message label,
                    @NonNull boolean checked) implements FormElement {

    /**
     *
     * @return whether the radio button is checked
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     *
     * @return the value of the input radio element
     */
    @NonNull
    public String getValue() {
        return value;
    }

    /**
     *
     * @return It defines an identifier (ID) which must be unique in the whole document
     */
    @NonNull
    public String getId() {
        return id;
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
     * @return A Radio Builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A Radio Builder.
     */
    public static class Builder {

        private String value;
        private String id;
        private Message label;

        private boolean checked;

        /**
         *
         * @param value the value of the input radio element
         * @return The Builder
         */
        public Builder value(@Nullable String value) {
            this.value = value;
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
         * @param checked whether the radio button is checked
         * @return The Builder
         */
        @NonNull
        public Builder checked(boolean checked) {
            this.checked = checked;
            return this;
        }

        /**
         *
         * @return creates the radio button
         */
        @NonNull
        public Radio build() {
            return new Radio(value, id, label, checked);
        }
    }
}

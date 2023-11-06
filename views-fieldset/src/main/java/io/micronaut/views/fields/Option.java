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

/**
 * HTML Option.
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/option">Option</a>
 * @param selected If present, this Boolean attribute indicates that the option is initially selected.
 * @param disabled If this Boolean attribute is set, this option is not checkable.
 * @param value The content of this attribute represents the value to be submitted with the form, should this option be selected.
 * @param label This attribute is text for the label indicating the meaning of the option.
 *
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Experimental
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = Option.Builder.class))
public record Option(boolean disabled,
                     boolean selected,
                     @NonNull String value,
                     @NonNull Message label) implements FormElement {

    /**
     *
     * @return Option Builder.
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    @Override
    @NonNull
    public String getTag() {
        return HtmlTag.TAG_OPTION;
    }

    /**
     * Option Builder.
     */
    public static class Builder {

        private boolean disabled;
        private String value;
        private boolean selected;
        private Message label;

        /**
         *
         * @param disabled If this Boolean attribute is set, this option is not checkable.
         * @return the Builder
         */
        @NonNull
        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        /**
         *
         * @param selected If present, this Boolean attribute indicates that the option is initially selected.
         * @return the Builder
         */
        @NonNull
        public Builder selected(boolean selected) {
            this.selected = selected;
            return this;
        }

        /**
         *
         * @param value The content of this attribute represents the value to be submitted with the form, should this option be selected.
         * @return The Builder
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
         * Instantiates an Option.
         * @return An Option.
         */
        @NonNull
        public Option build() {
            return new Option(disabled, selected, value, label);
        }
    }
}

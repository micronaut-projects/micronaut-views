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
import io.micronaut.views.fields.render.InputType;

import java.util.Collections;
import java.util.List;

/**
 * Input Radio.
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/radio">Input Radio</a>
 * @param name Name of the form control. Submitted with the form as part of a name/value pair
 * @param required If true indicates that the user must specify a value for the input before the owning form can be submitted.
 * @param buttons Radio Buttons
 * @param label HTML label
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = InputRadioFormElement.Builder.class))
public record InputRadioFormElement(@NonNull String name,
                                    boolean required,
                                    @NonNull List<Radio> buttons,
                                    @NonNull Message label) implements InputFormElement {
    @Override
    @NonNull
    public String getType() {
        return InputType.ATTR_TYPE_RADIO;
    }

    /**
     *
     * @return Creates a {@link InputRadioFormElement.Builder}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * An {@link InputRadioFormElement} Builder.
     */
    @SuppressWarnings("Duplicates") // Builders by definition have duplicated code which sonar does not like
    public static class Builder {
        private List<Radio> buttons;

        private String name;

        private boolean required;

        private Message label;

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
         * @param buttons Radio Buttons
         * @return The Builder
         */
        public Builder buttons(@NonNull List<Radio> buttons) {
            this.buttons = buttons;
            return this;
        }

        /**
         *
         * @return Instantiates a {@link InputRadioFormElement}.
         */
        public InputRadioFormElement build() {
            return new InputRadioFormElement(name, required, buttons == null ? Collections.emptyList() : buttons, label);
        }
    }
}

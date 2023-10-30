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

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/submit">input submit</a>
 * @param value a string which is displayed as the button's label.
 *
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = InputSubmitFormElement.Builder.class))
public record InputSubmitFormElement(@NonNull Message value) implements InputFormElement {
    @Override
    @NonNull
    public String getType() {
        return InputType.ATTR_TYPE_SUBMIT;
    }

    /**
     *
     * @return {@link InputSubmitFormElement} builder.
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * {@link InputSubmitFormElement} Builder.
     */
    @SuppressWarnings("Duplicates") // Builders by definition have duplicated code which sonar does not like
    public static class Builder {
        @NonNull
        private Message value;

        /**
         *
         * @param value Name of the form control. Submitted with the form as part of a name/value pair
         * @return The Builder
         */
        @NonNull
        public Builder value(@NonNull Message value) {
            this.value = value;
            return this;
        }

        /**
         *
         * @return Creates a {@link InputSubmitFormElement}.
         */
        @NonNull
        public InputSubmitFormElement build() {
            return new InputSubmitFormElement(value);
        }
    }
}

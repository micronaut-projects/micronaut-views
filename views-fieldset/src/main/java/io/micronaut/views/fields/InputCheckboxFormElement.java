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
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/checkbox">Input Checkbox</a>
 * @param checkboxes Checkboxes
 *
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = InputCheckboxFormElement.Builder.class))
public record InputCheckboxFormElement(@NonNull List<Checkbox> checkboxes) implements FormElement {

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
         * @return Creates a {@link InputCheckboxFormElement}
         */
        @NonNull
        public InputCheckboxFormElement build() {
            return new InputCheckboxFormElement(checkboxes == null ? Collections.emptyList() : checkboxes);
        }
    }
}

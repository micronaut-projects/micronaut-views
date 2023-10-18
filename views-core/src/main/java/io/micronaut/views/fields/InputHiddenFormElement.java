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

import java.util.Objects;

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/hidden">input hidden</a>
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = InputHiddenFormElement.Builder.class))
public class InputHiddenFormElement implements FormElement {
    @NonNull
    private final String name;

    @NonNull
    private final String value;

    /**
     *
     * @param name Name of the form control. Submitted with the form as part of a name/value pair
     * @param value A string representing the value of the hidden field.
     */
    public InputHiddenFormElement(@NonNull String name,
                                  @NonNull String value) {
        this.name = name;
        this.value = value;
    }

    /**
     *
     * @return Name of the form control. Submitted with the form as part of a name/value pair
     */
    @NonNull
    public String getName() {
        return name;
    }

    /**
     *
     * @return A string representing the value of the hidden field
     */
    @NonNull
    public String getValue() {
        return value;
    }

    @SuppressWarnings("NeedBraces")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InputHiddenFormElement that)) return false;

        if (!Objects.equals(name, that.name)) return false;
        return Objects.equals(value, that.value);
    }

    @SuppressWarnings("NeedBraces")
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    /**
     *
     * @return {@link InputHiddenFormElement} builder.
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * {@link InputHiddenFormElement} Builder.
     */
    public static class Builder {
        @NonNull
        private String name;

        @NonNull
        private String value;

        /**
         *
         * @param value Name of the form control. Submitted with the form as part of a name/value pair
         * @return The Builder
         */
        @NonNull
        public Builder value(@NonNull String value) {
            this.value = value;
            return this;
        }

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
         * @return Creates a {@link InputHiddenFormElement}.
         */
        @NonNull
        public InputHiddenFormElement build() {
            return new InputHiddenFormElement(name, value);
        }
    }
}

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

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/hidden">input hidden</a>
 */
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = InputHiddenFormElement.Builder.class))
public class InputHiddenFormElement extends FormElement {
    @NonNull
    private final String name;

    @NonNull
    private final String value;

    public InputHiddenFormElement(@NonNull String name,
                                  @NonNull String value) {
        this.name = name;
        this.value = value;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getValue() {
        return value;
    }

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        @NonNull
        private String name;

        @NonNull
        private String value;

        @NonNull
        public Builder value(@NonNull String value) {
            this.value = value;
            return this;
        }

        @NonNull
        public Builder name(@NonNull String name) {
            this.name = name;
            return this;
        }

        @NonNull
        public InputHiddenFormElement build() {
            return new InputHiddenFormElement(name, value);
        }
    }
}

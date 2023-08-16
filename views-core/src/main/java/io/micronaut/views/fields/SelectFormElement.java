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
import java.util.Objects;

@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = SelectFormElement.Builder.class))
public class SelectFormElement extends FormElement {
    @NonNull
    private final String name;

    @Nullable
    private final String id;

    @NonNull
    private final List<Option> options;


    /**
     *
     * @param name The name attribute.
     * @param id The id attribute.
     */
    public SelectFormElement(@NonNull String name,
                             @Nullable String id,
                             @NonNull List<Option> options) {
        this.name = name;
        this.id = id;
        this.options = options;
    }

    /*
     *
     * @return The name of the control.
     */
    @NonNull
    public String getName() {
        return this.name;
    }

    /**
     *
     * @return An id attribute. Often used to allow the form element to be associated with a label element for accessibility purposes.
     */
    @Nullable
    public String getId() {
        return this.id;
    }

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    @NonNull
    public List<Option> getOptions() {
        return options;
    }

    public static class Builder {

        @NonNull
        private String name;

        @Nullable
        private String id;

        private List<Option> options;

        @NonNull
        public Builder name(@NonNull String name) {
            this.name = name;
            return this;
        }

        @NonNull
        public Builder id(@Nullable String id) {
            this.id = id;
            return this;
        }

        @NonNull
        public Builder options(List<Option> options) {
            this.options = options;
            return this;
        }

        @NonNull
        SelectFormElement build() {
            return new SelectFormElement(
                    Objects.requireNonNull(name),
                    id,
                    options == null ? Collections.emptyList() : options);
        }
    }
}

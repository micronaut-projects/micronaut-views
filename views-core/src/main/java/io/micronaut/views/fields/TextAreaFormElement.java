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

import java.util.Objects;

/**
 * The textarea HTML element represents a multi-line plain-text editing control.
 */
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = TextAreaFormElement.Builder.class))
public class TextAreaFormElement extends FormElement {
    @NonNull
    private final String name;

    @Nullable
    private final String id;

    @Nullable
    private final Integer rows;

    @Nullable
    private final Integer cols;

    @Nullable
    private final String content;

    /**
     *
     * @param name The name attribute.
     * @param id The id attribute.
     * @param rows The number of visible text lines for the control.
     * @param cols The visible width of the text control, in average character widths.
     * @param content The textarea content.
     */
    public TextAreaFormElement(@NonNull String name,
                               @Nullable String id,
                               @Nullable Integer rows,
                               @Nullable Integer cols,
                               @Nullable String content) {
        this.name = name;
        this.id = id;
        this.rows = rows;
        this.cols = cols;
        this.content = content;
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

    /**
     *
     * @return The number of visible text lines for the control.
     */
    @Nullable
    public Integer getRows() {
        return rows;
    }

    /**
     *
     * @return The visible width of the text control, in average character widths.
     */
    @Nullable
    public Integer getCols() {
        return cols;
    }

    /**
     *
     * @return The textarea content.
     */
    @Nullable
    public String getContent() {
        return content;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        @NonNull
        private String name;
        @Nullable
        private String id;
        @Nullable
        private Integer rows;
        @Nullable
        private Integer cols;
        @Nullable
        private String content;

        @NonNull
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        @NonNull
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        @NonNull
        public Builder rows(Integer rows) {
            this.rows = rows;
            return this;
        }

        @NonNull
        public Builder cols(Integer cols) {
            this.cols = cols;
            return this;
        }


        @NonNull
        public Builder content(String content) {
            this.content = content;
            return this;
        }

        @NonNull
        public TextAreaFormElement build() {
            return new TextAreaFormElement(Objects.requireNonNull(name),
                id,
                rows,
                cols,
                content);
        }
    }
}

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

/**
 * Text Area.
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/textarea">Textarea</a>
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = TextareaFormElement.Builder.class))
public class TextareaFormElement implements FormElement, FormElementAttributes, GlobalAttributes {
    @NonNull
    private final String name;

    @Nullable
    private final String id;

    @Nullable
    private final Integer cols;

    @Nullable
    private final Integer rows;

    /**
     * The placeholder attribute is a string that provides a brief hint to the user as to what kind of information is expected in the field.
     */
    @Nullable
    private final String placeholder;

    private final boolean required;

    private final boolean readOnly;

    @Nullable
    private final String value;

    @Nullable
    private final Message label;

    @NonNull
    private final List<Message> errors;

    /**
     *
     * @param name Name of the form control. Submitted with the form as part of a name/value pair
     * @param id It defines an identifier (ID) which must be unique in the whole document
     * @param cols The visible width of the text control, in average character widths
     * @param rows The number of visible text lines for the control.
     * @param placeholder A hint to the user of what can be entered in the control
     * @param required If true indicates that the user must specify a value for the input before the owning form can be submitted.
     * @param readOnly indicates that the user cannot modify the value of the control
     * @param value text area content
     * @param label represents a caption for an item in a user interface
     * @param errors Form element validation Errors.
     */
    public TextareaFormElement(@NonNull String name,
                               @NonNull String id,
                               @Nullable Integer cols,
                               @Nullable Integer rows,
                               @Nullable String placeholder,
                               boolean required,
                               boolean readOnly,
                               @Nullable String value,
                               @Nullable Message label,
                               @NonNull List<Message> errors) {
        this.name = name;
        this.id = id;
        this.cols = cols;
        this.rows = rows;
        this.placeholder = placeholder;
        this.required = required;
        this.readOnly = readOnly;
        this.value = value;
        this.label = label;
        this.errors = errors;
    }

    @Override
    @NonNull
    public String getName() {
        return name;
    }

    @Override
    @Nullable
    public String getId() {
        return id;
    }

    /**
     *
     * @return The placeholder attribute is a string that provides a brief hint to the user as to what kind of information is expected in the field.
     */
    @Nullable
    public String getPlaceholder() {
        return placeholder;
    }

    /**
     *
     * @return A Boolean attribute which, if present, means this field cannot be edited by the user.
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     *
     * @return If true indicates that the user must specify a value for the input before the owning form can be submitted.
     */
    public boolean isRequired() {
        return required;
    }

    /**
     *
     * @return The visible width of the text control, in average character widths
     */
    @Nullable
    public Integer getCols() {
        return cols;
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
     * @return text area content
     */
    @Nullable
    public String getValue() {
        return value;
    }

    @Override
    @Nullable
    public Message getLabel() {
        return label;
    }

    @Override
    @NonNull
    public List<Message> getErrors() {
        return errors;
    }

    @SuppressWarnings("NeedBraces")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextareaFormElement that)) return false;

        if (required != that.required) return false;
        if (readOnly != that.readOnly) return false;
        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(cols, that.cols)) return false;
        if (!Objects.equals(rows, that.rows)) return false;
        if (!Objects.equals(placeholder, that.placeholder))
            return false;
        if (!Objects.equals(value, that.value)) return false;
        if (!Objects.equals(label, that.label)) return false;
        return Objects.equals(errors, that.errors);
    }

    @SuppressWarnings("NeedBraces")
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (cols != null ? cols.hashCode() : 0);
        result = 31 * result + (rows != null ? rows.hashCode() : 0);
        result = 31 * result + (placeholder != null ? placeholder.hashCode() : 0);
        result = 31 * result + (required ? 1 : 0);
        result = 31 * result + (readOnly ? 1 : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (errors != null ? errors.hashCode() : 0);
        return result;
    }

    /**
     *
     * @return TextArea FormElement Builder
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

        private String placeholder;

        private boolean required;

        private boolean readOnly;

        private Integer rows;

        private Integer cols;

        private String value;

        private List<Message> errors;

        private Message label;

        /**
         *
         * @param required If true indicates that the user must specify a value for the input before the owning form can be submitted.
         * @return The Builder
         */
        @NonNull
        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        /**
         *
         * @param readOnly indicates that the user cannot modify the value of the control
         * @return The Builder
         */
        @NonNull
        public Builder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        /**
         *
         * @param rows The number of visible text lines for the control.
         * @return The Builder
         */
        @NonNull
        public Builder rows(@NonNull Integer rows) {
            this.rows = rows;
            return this;
        }

        /**
         *
         * @param cols The visible width of the text control, in average character widths
         * @return the Builder
         */
        @NonNull
        public Builder cols(@NonNull Integer cols) {
            this.cols = cols;
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
         * @param placeholder The placeholder attribute is a string that provides a brief hint to the user as to what kind of information is expected in the field.
         * @return the Builder
         */
        @NonNull
        public Builder placeholder(@NonNull String placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        /**
         *
         * @param value text area content
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
         * @return Creates a TextAreaFormElement.
         */
        @NonNull
        public TextareaFormElement build() {
            return new TextareaFormElement(name,
                id,
                cols,
                rows,
                placeholder,
                required,
                readOnly,
                value,
                label,
                errors != null ? errors : Collections.emptyList());
        }
    }
}

package io.micronaut.views.fields;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input">Input Form Element</a>
 */
@Introspected(builder = @Introspected.IntrospectionBuilder(
    builderClass = InputField.Builder.class
))
public class InputField implements InputAttributes {

    @NonNull
    private final String name;

    private final boolean required;

    @NonNull
    private final InputType type;


    @Nullable
    private final String id;

    @Nullable
    private final String value;

    @NonNull
    private final List<Message> errors;

    @Nullable

    private final Message label;
    /**
     * @param name  name of the form control
     */
    InputField(@NonNull String name,
               @NonNull InputType type,
               boolean required,
               @Nullable String id,
               @Nullable String value,
               @Nullable Message label,
               @NonNull List<Message> errors) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.id = id;
        this.value = value;
        this.label = label;
        this.errors = errors;
    }


    public static Builder builder() {
        return new Builder();
    }

    @Override
    @NonNull
    public InputType getType() {
        return type;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public List<Message> getErrors() {
        return errors;
    }

    @Override
    public boolean hasErrors() {
        return CollectionUtils.isNotEmpty(errors);
    }

    @Override
    @NonNull
    public Message getLabel() {
        return label;
    }

    @Override
    @Nullable
    public String getId() {
        return this.id;
    }

    @Override
    @NonNull
    public String getName() {
        return this.name;
    }

    @Nullable
    public String getValue() {
        return this.value;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InputField field)) return false;

        if (required != field.required) return false;
        if (!Objects.equals(name, field.name)) return false;
        if (type != field.type) return false;
        if (!Objects.equals(id, field.id)) return false;
        if (!Objects.equals(value, field.value)) return false;
        if (!Objects.equals(errors, field.errors)) return false;
        return Objects.equals(label, field.label);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (required ? 1 : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (errors != null ? errors.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        return result;
    }

    public static class Builder {

        @NonNull
        private String name;


        @NonNull
        private String id;

        @Nullable
        private String value;

        @NonNull
        private InputType type;

        private boolean required;

        private List<Message> errors;

        private Message label;

        @NonNull
        public Builder errors(@NonNull List<Message> errors) {
            this.errors = errors;
            return this;
        }

        @NonNull
        public Builder value(@Nullable String value) {
            this.value = value;
            return this;
        }

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

        /**
         *
         * @param required Whether the input value is required or must be checked
         * @return The Builder
         */
        @NonNull
        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        @NonNull
        public Builder label(Message label) {
            this.label = label;
            return this;
        }

        @NonNull
        public Builder type(@NonNull InputType type) {
            this.type = type;
            return this;
        }

        public InputField build() {
            return new InputField(name, type, required, id, value, label, errors == null ? Collections.emptyList() : errors);
        }
    }
}

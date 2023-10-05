package io.micronaut.views.fields;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = Checkbox.Builder.class))
public class Checkbox extends FormElement {

    @NonNull
    private final String name;

    @NonNull
    private final String value;

    private final boolean checked;

    private final boolean required;

    @Nullable
    private final String id;

    @Nullable
    private final Message label;

    @NonNull
    private final List<Message> errors;


    public Checkbox(@NonNull String name,
                    String value,
                    boolean checked,
                    boolean required,
                    @Nullable String id,
                    @Nullable Message label,
                    @NonNull List<Message> errors) {
        this.name = name;
        this.value = value;
        this.checked = checked;
        this.required = required;
        this.id = id;
        this.label = label;
        this.errors = errors;
    }

    public boolean isRequired() {
        return required;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getValue() {
        return value;
    }

    public boolean isChecked() {
        return checked;
    }

    @Nullable
    public String getId() {
        return id;
    }

    @Nullable
    public Message getLabel() {
        return label;
    }

    @NonNull
    public List<Message> getErrors() {
        return errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Checkbox checkbox)) return false;

        if (checked != checkbox.checked) return false;
        if (required != checkbox.required) return false;
        if (!Objects.equals(name, checkbox.name)) return false;
        if (!Objects.equals(value, checkbox.value)) return false;
        if (!Objects.equals(id, checkbox.id)) return false;
        if (!Objects.equals(label, checkbox.label)) return false;
        return Objects.equals(errors, checkbox.errors);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (checked ? 1 : 0);
        result = 31 * result + (required ? 1 : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (errors != null ? errors.hashCode() : 0);
        return result;
    }

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String name;

        private String id;

        private boolean checked;

        private boolean required;

        private String value;

        private List<Message> errors;

        private Message label;

        @NonNull
        public Builder name(@NonNull String name) {
            this.name = name;
            return this;
        }

        @NonNull
        public Builder id(@NonNull String id) {
            this.id = id;
            return this;
        }

        /**
         *
         * @param value The value attribute of the input element
         * @return the Builder
         */
        @NonNull
        public Builder value(@NonNull String value) {
            this.value = value;
            return this;
        }

        @NonNull
        public Builder label(Message label) {
            this.label = label;
            return this;
        }

        @NonNull
        public Builder errors(@NonNull List<Message> errors) {
            this.errors = errors;
            return this;
        }

        @NonNull
        public Builder checked(boolean checked) {
            this.checked = checked;
            return this;
        }

        @NonNull
        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        @NonNull
        public Checkbox build() {
            return new Checkbox(name,
                value,
                checked,
                required,
                id,
                label,
                errors == null ? Collections.emptyList() : errors);
        }
    }
}

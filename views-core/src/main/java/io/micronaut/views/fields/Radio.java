package io.micronaut.views.fields;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

import java.util.Objects;

@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = Radio.Builder.class))
public class Radio {
    @NonNull
    private final String value;

    @NonNull
    private final String id;

    @NonNull
    private final Message label;

    @NonNull
    private final boolean checked;

    public Radio(String value,
                 String id,
                 Message label,
                 boolean checked) {
        this.value = value;
        this.id = id;
        this.label = label;
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    @NonNull
    public String getValue() {
        return value;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public Message getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Radio radio)) return false;

        if (checked != radio.checked) return false;
        if (!Objects.equals(value, radio.value)) return false;
        if (!Objects.equals(id, radio.id)) return false;
        return Objects.equals(label, radio.label);
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (checked ? 1 : 0);
        return result;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String value;
        private String id;
        private Message label;

        private boolean checked;

        public Builder value(@Nullable String value) {
            this.value = value;
            return this;
        }

        @NonNull
        public Builder id(@Nullable String id) {
            this.id = id;
            return this;
        }

        @NonNull
        public Builder label(Message label) {
            this.label = label;
            return this;
        }

        @NonNull
        public Builder checked(boolean checked) {
            this.checked = checked;
            return this;
        }

        @NonNull
        public Radio build() {
            return new Radio(value, id, label, checked);
        }

    }
}

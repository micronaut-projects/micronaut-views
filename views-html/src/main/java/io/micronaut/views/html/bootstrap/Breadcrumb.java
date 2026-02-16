package io.micronaut.views.html.bootstrap;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@Serdeable
public record Breadcrumb(@NonNull @NotBlank String text,
                         @NonNull @NotNull Boolean active,
                         @Nullable String href) {

    public Breadcrumb(@NonNull String text) {
        this(text, false, null);
    }

    public Breadcrumb(@NonNull String text, @NonNull Boolean active) {
        this(text, active, null);
    }

    public Breadcrumb(@NonNull @NotBlank String text, @Nullable String href) {
        this(text, false, href);
    }

    public static @NonNull Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String text;
        private boolean active;
        private String href;

        public @NonNull Builder href(@NonNull String href) {
            this.href = href;
            return this;
        }

        public @NonNull Builder active() {
            return active(true);
        }

        public @NonNull Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public @NonNull Builder text(@NonNull String text) {
            this.text = text;
            return this;
        }

        public @NonNull Breadcrumb build() {
            return new Breadcrumb(Objects.requireNonNull(text), active, href);
        }
    }
}

package io.micronaut.views.fields;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/radio">Input Radio</a>
 */
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = InputRadioFormElement.Builder.class))
public class InputRadioFormElement extends FormElement {
    @NonNull
    private final String name;

    private final boolean required;

    @NonNull
    private final List<Radio> buttons;

    public InputRadioFormElement(@NonNull String name,
                                 boolean required,
                                 @NonNull List<Radio> buttons) {
        this.name = name;
        this.required = required;
        this.buttons = buttons;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    @NonNull
    public List<Radio> getButtons() {
        return buttons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InputRadioFormElement that)) return false;

        if (required != that.required) return false;
        if (!Objects.equals(name, that.name)) return false;
        return Objects.equals(buttons, that.buttons);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (required ? 1 : 0);
        result = 31 * result + (buttons != null ? buttons.hashCode() : 0);
        return result;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<Radio> buttons;

        private String name;

        private boolean required;

        @NonNull
        public Builder name(@NonNull String name) {
            this.name = name;
            return this;
        }

        @NonNull
        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        public Builder buttons(@NonNull List<Radio> buttons) {
            this.buttons = buttons;
            return this;
        }

        public InputRadioFormElement build() {
            return new InputRadioFormElement(name, required, buttons == null ? Collections.emptyList() : buttons);
        }
    }
}

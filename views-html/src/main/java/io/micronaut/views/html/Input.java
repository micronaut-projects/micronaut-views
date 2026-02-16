package io.micronaut.views.html;

import io.micronaut.core.annotation.Introspected;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@Introspected
public class Input extends AbstractHtmlElement {
    private static final String TAG = "input";

    protected Input(@NonNull Map<@NonNull String, @NonNull String> attributes,
                    @Nullable String content,
                    @Nullable List<HtmlElement> elements) {
        super(attributes, content, elements);
    }

    public static @NonNull Builder builder() {
        return new Builder();
    }

    @Override
    public @NonNull String getTag() {
        return TAG;
    }

    public static class Builder extends HtmlElementBuilder<Builder> {
        private static final String TYPE = "type";
        private static final String NAME = "name";
        private static final String VALUE = "value";
        private static final String PLACEHOLDER = "placeholder";
        private static final String REQUIRED = "required";
        private static final String DISABLED = "disabled";
        private static final String READONLY = "readonly";
        private static final String TYPE_BUTTON = "button";
        private static final String TYPE_CHECKBOX = "checkbox";
        private static final String TYPE_COLOR = "color";
        private static final String TYPE_DATE = "date";
        private static final String TYPE_DATETIME_LOCAL = "datetime-local";
        private static final String TYPE_EMAIL = "email";
        private static final String TYPE_FILE = "file";
        private static final String TYPE_HIDDEN = "hidden";
        private static final String TYPE_IMAGE = "image";
        private static final String TYPE_MONTH = "month";
        private static final String TYPE_NUMBER = "number";
        private static final String TYPE_PASSWORD = "password";
        private static final String TYPE_RADIO = "radio";
        private static final String TYPE_RANGE = "range";
        private static final String TYPE_RESET = "reset";
        private static final String TYPE_SEARCH = "search";
        private static final String TYPE_SUBMIT = "submit";
        private static final String TYPE_TEL = "tel";
        private static final String TYPE_TEXT = "text";
        private static final String TYPE_TIME = "time";
        private static final String TYPE_URL = "url";
        private static final String TYPE_WEEK = "week";

        public @NonNull Builder type(@NonNull String type) {
            return attribute(TYPE, type);
        }

        public @NonNull Builder button() {
            return type(TYPE_BUTTON);
        }

        public @NonNull Builder checkbox() {
            return type(TYPE_CHECKBOX);
        }

        public @NonNull Builder color() {
            return type(TYPE_COLOR);
        }

        public @NonNull Builder date() {
            return type(TYPE_DATE);
        }

        public @NonNull Builder datetimeLocal() {
            return type(TYPE_DATETIME_LOCAL);
        }

        public @NonNull Builder email() {
            return type(TYPE_EMAIL);
        }

        public @NonNull Builder file() {
            return type(TYPE_FILE);
        }

        public @NonNull Builder hidden() {
            return type(TYPE_HIDDEN);
        }

        public @NonNull Builder image() {
            return type(TYPE_IMAGE);
        }

        public @NonNull Builder month() {
            return type(TYPE_MONTH);
        }

        public @NonNull Builder number() {
            return type(TYPE_NUMBER);
        }

        public @NonNull Builder password() {
            return type(TYPE_PASSWORD);
        }

        public @NonNull Builder radio() {
            return type(TYPE_RADIO);
        }

        public @NonNull Builder range() {
            return type(TYPE_RANGE);
        }

        public @NonNull Builder reset() {
            return type(TYPE_RESET);
        }

        public @NonNull Builder search() {
            return type(TYPE_SEARCH);
        }

        public @NonNull Builder submit() {
            return type(TYPE_SUBMIT);
        }

        public @NonNull Builder tel() {
            return type(TYPE_TEL);
        }

        public @NonNull Builder text() {
            return type(TYPE_TEXT);
        }

        public @NonNull Builder time() {
            return type(TYPE_TIME);
        }

        public @NonNull Builder url() {
            return type(TYPE_URL);
        }

        public @NonNull Builder week() {
            return type(TYPE_WEEK);
        }

        public @NonNull Builder name(@NonNull String name) {
            return attribute(NAME, name);
        }

        public @NonNull Builder value(@NonNull String value) {
            return attribute(VALUE, value);
        }

        public @NonNull Builder placeholder(@NonNull String placeholder) {
            return attribute(PLACEHOLDER, placeholder);
        }

        public @NonNull Builder required() {
            return attribute(REQUIRED, "required");
        }

        public @NonNull Builder disabled() {
            return attribute(DISABLED, "disabled");
        }

        public @NonNull Builder readonly() {
            return attribute(READONLY, "readonly");
        }

        public Input build() {
            return new Input(attributes, content, elements);
        }
    }
}

package io.micronaut.views.html;

import io.micronaut.core.annotation.Introspected;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@Introspected
public class Textarea extends AbstractHtmlElement {
    private static final String TAG = "textarea";

    protected Textarea(@NonNull Map<@NonNull String, @NonNull String> attributes,
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
        private static final String NAME = "name";
        private static final String ROWS = "rows";
        private static final String COLS = "cols";
        private static final String PLACEHOLDER = "placeholder";
        private static final String REQUIRED = "required";
        private static final String DISABLED = "disabled";
        private static final String READONLY = "readonly";

        public @NonNull Builder name(@NonNull String name) {
            return attribute(NAME, name);
        }

        public @NonNull Builder rows(@NonNull String rows) {
            return attribute(ROWS, rows);
        }

        public @NonNull Builder cols(@NonNull String cols) {
            return attribute(COLS, cols);
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

        public Textarea build() {
            return new Textarea(attributes, content, elements);
        }
    }
}

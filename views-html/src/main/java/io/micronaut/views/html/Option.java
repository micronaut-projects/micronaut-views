package io.micronaut.views.html;

import io.micronaut.core.annotation.Introspected;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@Introspected
public class Option extends AbstractHtmlElement {
    private static final String TAG = "option";

    protected Option(@NonNull Map<@NonNull String, @NonNull String> attributes,
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
        private static final String VALUE = "value";
        private static final String SELECTED = "selected";
        private static final String DISABLED = "disabled";

        public @NonNull Builder value(@NonNull String value) {
            return attribute(VALUE, value);
        }

        public @NonNull Builder selected() {
            return attribute(SELECTED, "selected");
        }

        public @NonNull Builder disabled() {
            return attribute(DISABLED, "disabled");
        }

        public Option build() {
            return new Option(attributes, content, elements);
        }
    }
}

package io.micronaut.views.html;

import io.micronaut.core.annotation.Introspected;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@Introspected
public class Button extends AbstractHtmlElement {
    private static final String TAG = "button";

    protected Button(@NonNull Map<@NonNull String, @NonNull String> attributes,
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
        private static final String DISABLED = "disabled";

        public @NonNull Builder type(@NonNull String type) {
            return attribute(TYPE, type);
        }

        public @NonNull Builder name(@NonNull String name) {
            return attribute(NAME, name);
        }

        public @NonNull Builder value(@NonNull String value) {
            return attribute(VALUE, value);
        }

        public @NonNull Builder disabled() {
            return attribute(DISABLED, "disabled");
        }

        public Button build() {
            return new Button(attributes, content, elements);
        }
    }
}

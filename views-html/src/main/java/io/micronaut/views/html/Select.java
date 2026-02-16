package io.micronaut.views.html;

import io.micronaut.core.annotation.Introspected;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@Introspected
public class Select extends AbstractHtmlElement {
    private static final String TAG = "select";

    protected Select(@NonNull Map<@NonNull String, @NonNull String> attributes,
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
        private static final String REQUIRED = "required";
        private static final String DISABLED = "disabled";
        private static final String MULTIPLE = "multiple";

        public @NonNull Builder name(@NonNull String name) {
            return attribute(NAME, name);
        }

        public @NonNull Builder required() {
            return attribute(REQUIRED, "required");
        }

        public @NonNull Builder disabled() {
            return attribute(DISABLED, "disabled");
        }

        public @NonNull Builder multiple() {
            return attribute(MULTIPLE, "multiple");
        }

        public @NonNull Builder option(@NonNull Option option) {
            element(option);
            return this;
        }

        public Select build() {
            return new Select(attributes, content, elements);
        }
    }
}

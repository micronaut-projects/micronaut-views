package io.micronaut.views.html;

import io.micronaut.core.annotation.Introspected;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@Introspected
public class Form extends AbstractHtmlElement {
    private static final String TAG = "form";

    protected Form(@NonNull Map<@NonNull String, @NonNull String> attributes,
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
        private static final String ACTION = "action";
        private static final String METHOD = "method";
        private static final String ENCTYPE = "enctype";

        public @NonNull Builder action(@NonNull String action) {
            return attribute(ACTION, action);
        }

        public @NonNull Builder method(@NonNull String method) {
            return attribute(METHOD, method);
        }

        public @NonNull Builder enctype(@NonNull String enctype) {
            return attribute(ENCTYPE, enctype);
        }

        public Form build() {
            return new Form(attributes, content, elements);
        }
    }
}

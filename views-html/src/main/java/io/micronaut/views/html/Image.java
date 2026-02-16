package io.micronaut.views.html;

import io.micronaut.core.annotation.Introspected;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@Introspected
public class Image extends AbstractHtmlElement {
    private static final String TAG = "img";

    protected Image(@NonNull Map<@NonNull String, @NonNull String> attributes,
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
        private static final String SRC = "src";
        private static final String ALT = "alt";
        private static final String WIDTH = "width";
        private static final String HEIGHT = "height";

        public @NonNull Builder src(@NonNull String src) {
            return attribute(SRC, src);
        }

        public @NonNull Builder alt(@NonNull String alt) {
            return attribute(ALT, alt);
        }

        public @NonNull Builder width(@NonNull String width) {
            return attribute(WIDTH, width);
        }

        public @NonNull Builder height(@NonNull String height) {
            return attribute(HEIGHT, height);
        }

        public Image build() {
            return new Image(attributes, content, elements);
        }
    }
}

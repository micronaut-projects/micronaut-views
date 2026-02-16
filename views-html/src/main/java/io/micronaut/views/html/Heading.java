package io.micronaut.views.html;

import io.micronaut.core.annotation.Introspected;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@Introspected
public class Heading extends AbstractHtmlElement {

    private final int level;

    protected Heading(int level,
                      @NonNull Map<@NonNull String, @NonNull String> attributes,
                      @Nullable String content,
                      @Nullable List<HtmlElement> elements) {
        super(attributes, content, elements);
        if (level < 1 || level > 6) {
            throw new IllegalArgumentException("Heading level must be between 1 and 6");
        }
        this.level = level;
    }

    public static @NonNull Builder builder() {
        return new Builder();
    }

    @Override
    public @NonNull String getTag() {
        return "h" + level;
    }

    public int getLevel() {
        return level;
    }

    public static class Builder extends HtmlElementBuilder<Builder> {
        private int level = 1;

        public @NonNull Builder level(int level) {
            this.level = level;
            return this;
        }

        public Heading build() {
            return new Heading(level, attributes, content, elements);
        }
    }
}

package io.micronaut.views.html;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.List;
import java.util.Map;

public abstract class AbstractHtmlElement implements HtmlElement {
    private final @NonNull Map<@NonNull String, @NonNull String> attributes;
    private final @Nullable String content;
    private final @Nullable List<HtmlElement> elements;

    protected AbstractHtmlElement(@NonNull Map<@NonNull String, @NonNull String> attributes,
                                  @Nullable String content,
                                  @Nullable List<HtmlElement> elements) {
        this.attributes = attributes;
        this.content = content;
        this.elements = elements;
    }

    @Override
    public @Nullable List<? extends HtmlElement> getElements() {
        return elements;
    }

    @Override
    public @Nullable String getContent() {
        return content;
    }

    @Override
    public @NonNull Map<String, String> getAttributes() {
        return attributes;
    }
}

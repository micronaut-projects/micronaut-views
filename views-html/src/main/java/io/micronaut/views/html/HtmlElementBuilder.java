package io.micronaut.views.html;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class HtmlElementBuilder<T extends HtmlElementBuilder<T>> {
    protected Map<@NonNull String, @NonNull String> attributes = new LinkedHashMap<>();
    protected @Nullable String content;
    protected List<HtmlElement> elements;

    public @NonNull  T attribute(@NonNull String attributeName, @NonNull String attributeValue) {
        attributes.put(attributeName, attributeValue);
        return (T) this;
    }

    public @NonNull <E extends HtmlElement> T element(@NonNull E htmlElement) {
        if (elements == null) {
            elements = new ArrayList<>();
        }
        elements.add(htmlElement);
        return (T) this;
    }

    public @NonNull T content(@NonNull String content) {
        this.content = content;
        return (T) this;
    }

    public @NonNull T classAttribute(@NonNull String value) {
        return attribute("class", value);
    }

    public @NonNull T id(@NonNull String value) {
        return attribute("id", value);
    }
}

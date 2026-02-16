package io.micronaut.views.html;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public abstract class DelegateHtmlElement implements HtmlElement {
    protected final HtmlElement htmlElement;

    protected DelegateHtmlElement(HtmlElement htmlElement) {
        this.htmlElement = htmlElement;
    }

    @Override
    public @NonNull String getTag() {
        return htmlElement.getTag();
    }

    @Override
    public @NonNull Map<String, String> getAttributes() {
        return htmlElement.getAttributes();
    }

    @Override
    public @Nullable String getContent() {
        return htmlElement.getContent();
    }

    @Override
    public @Nullable List<? extends HtmlElement> getElements() {
        return htmlElement.getElements();
    }
}

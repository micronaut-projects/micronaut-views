package io.micronaut.views.html;

import io.micronaut.core.annotation.Introspected;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@Introspected
public class ListItem extends AbstractHtmlElement {
    private static final String TAG = "li";

    protected ListItem(@NonNull Map<@NonNull String, @NonNull String> attributes,
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
        public ListItem build() {
            return new ListItem(attributes, content, elements);
        }
    }
}

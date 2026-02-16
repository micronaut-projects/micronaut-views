package io.micronaut.views.html;

import io.micronaut.core.annotation.Introspected;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@Introspected
public class UnorderedList extends AbstractHtmlElement {
    private static final String TAG = "ul";

    protected UnorderedList(@NonNull Map<@NonNull String, @NonNull String> attributes,
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
        public UnorderedList build() {
            return new UnorderedList(attributes, content, elements);
        }

        public Builder li(ListItem listItem) {
            element(listItem);
            return this;
        }
    }
}

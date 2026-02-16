package io.micronaut.views.html;

import io.micronaut.core.annotation.Introspected;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@Introspected
public class Table extends AbstractHtmlElement {
    private static final String TAG = "table";

    public Table(@NonNull Map<@NonNull String, @NonNull String> attributes,
                 @Nullable String content,
                 @Nullable TableHead head,
                 @Nullable TableBody body) {
        super(attributes, content, List.of(head, body));
    }

    public @Nullable TableBody getBody() {
        return getElements()
                .stream()
                .filter(TableBody.class::isInstance)
                .map(TableBody.class::cast)
                .findFirst()
                .orElse(null);
    }

    public @Nullable TableHead getHead() {
        return getElements()
                .stream()
                .filter(TableHead.class::isInstance)
                .map(TableHead.class::cast)
                .findFirst()
                .orElse(null);
    }

    @Override
    public @NonNull String getTag() {
        return TAG;
    }

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends HtmlElementBuilder<Builder> {
        private TableHead head;
        private TableBody body;

        public @NonNull Builder head(@NonNull TableHead head) {
            this.head = head;
            return this;
        }

        public @NonNull Builder body(@NonNull TableBody body) {
            this.body = body;
            return this;
        }

        public @NonNull Table build() {
            return new Table(attributes, content, head, body);
        }
    }
}

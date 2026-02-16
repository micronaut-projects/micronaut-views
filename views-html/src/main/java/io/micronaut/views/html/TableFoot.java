package io.micronaut.views.html;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.util.CollectionUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Introspected
public class TableFoot extends AbstractHtmlElement {
    private static final String TAG = "tfoot";

    public TableFoot(@NonNull Map<@NonNull String, @NonNull String> attributes,
                     @Nullable String content,
                     @NonNull List<@NonNull TableRow> rows) {
        super(attributes, content, rows.stream().map(r -> ((HtmlElement) r)).toList());
    }

    public @NonNull List<@NonNull TableRow> getRows() {
        if (CollectionUtils.isEmpty(getElements())) {
            return Collections.emptyList();
        }
        return getElements()
                .stream()
                .filter(TableRow.class::isInstance)
                .map(TableRow.class::cast)
                .toList();
    }

    public static @NonNull Builder builder() {
        return new Builder();
    }

    @Override
    public @NonNull String getTag() {
        return TAG;
    }

    public static class Builder extends HtmlElementBuilder<Builder> {
        private List<@NonNull TableRow> rows = new ArrayList<>();

        public @NonNull Builder row(@NonNull TableRow row) {
            rows.add(row);
            return this;
        }

        public @NonNull TableFoot build() {
            return new TableFoot(attributes, content, rows);
        }
    }
}

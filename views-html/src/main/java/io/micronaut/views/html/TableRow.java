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
public class TableRow extends AbstractHtmlElement implements HtmlElement {
    private static final String TAG = "tr";

    protected TableRow(@NonNull Map<@NonNull String, @NonNull String> attributes,
                       @Nullable String content,
                       @NonNull List<@NonNull TableCell> cells) {
        super(attributes, content, cells.stream().map(r -> ((HtmlElement) r)).toList());
    }

    public @NonNull List<@NonNull TableCell> getCells() {
        if (CollectionUtils.isEmpty(getElements())) {
            return Collections.emptyList();
        }
        return getElements()
                .stream()
                .filter(TableCell.class::isInstance)
                .map(TableCell.class::cast)
                .toList();
    }

    @Override
    public @NonNull String getTag() {
        return TAG;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends HtmlElementBuilder<Builder> {
        private @NonNull List<@NonNull TableCell> cells = new ArrayList<>();

        public @NonNull Builder td(@NonNull TableData tableData) {
            cells.add(tableData);
            return this;
        }

        public @NonNull Builder tableData(@NonNull TableData tableData) {
            return td(tableData);
        }

        public @NonNull Builder th(@NonNull TableCellHeader tableCellHeader) {
            cells.add(tableCellHeader);
            return this;
        }

        public @NonNull Builder tableCellHeader(@NonNull TableCellHeader tableCellHeader) {
            return th(tableCellHeader);
        }

        public @NonNull Builder tableCell(@NonNull TableCell cell) {
            if (cell instanceof TableCellHeader th) {
                return th(th);
            } else if (cell instanceof TableData td) {
                return td(td);
            }
            return this;
        }

        public @NonNull TableRow build() {
            return new TableRow(attributes, content, cells);
        }


    }
}

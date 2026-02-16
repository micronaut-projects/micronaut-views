package io.micronaut.views.html.bootstrap;

import io.micronaut.views.html.*;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Supplier;

public final class TableUtils {
    private TableUtils() {

    }

    public static @NonNull Table createTable(@NonNull List<@NonNull String> columns,
                                             @NonNull Supplier<@NonNull List<@NonNull List<@NonNull TableCell>>> rowsProducer) {
        TableBody.Builder tableBodyBuilder = TableBody.builder();
        for (List<TableCell> row : rowsProducer.get()) {
            TableRow.Builder rowBuilder = TableRow.builder();
            for (TableCell cell : row) {
                rowBuilder.tableCell(cell);
            }
            tableBodyBuilder.row(rowBuilder.build());
        }
        TableRow.Builder tableHeadRowBuilder = TableRow.builder();
        for (String column : columns) {
            tableHeadRowBuilder.th(TableCellHeader.builder()
                    .attribute("scope", "col")
                    .content(column)
                    .build());
        }
        return Table.builder()
                .classAttribute("table table-striped")
                .head(TableHead.builder()
                        .row(tableHeadRowBuilder.build())
                        .build())
                .body(tableBodyBuilder.build())
                .build();
    }

}

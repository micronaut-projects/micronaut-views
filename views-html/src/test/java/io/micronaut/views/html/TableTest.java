package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TableTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Table.class));
    }

    @Test
    void tableToHtml() {
        String html = Table.builder()
                .classAttribute("table")
                .head(TableHead.builder()
                        .row(TableRow.builder()
                                .th(TableCellHeader.builder()
                                        .content("Observation")
                                        .build())
                                .build())
                .build())
                .body(TableBody.builder()
                        .row(TableRow.builder()
                                .td(TableData.builder().content("Tiredness").build())
                                .build())
                        .row(TableRow.builder()
                                .td(TableData.builder().content("Boredom").build())
                                .build())
                        .build())
                .build()
                .toHtml();
        String expected = """
                <table class="table"><thead><tr><th>Observation</th></tr></thead><tbody><tr><td>Tiredness</td></tr><tr><td>Boredom</td></tr></tbody></table>""";
        assertEquals(expected, html);
    }
}

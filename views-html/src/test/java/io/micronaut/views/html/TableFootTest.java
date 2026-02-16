package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TableFootTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(TableFoot.class));
    }

    @Test
    void toHtml() {
        String html = TableFoot.builder()
                .row(TableRow.builder()
                        .td(TableData.builder().content("Total").build())
                        .build())
                .build()
                .toHtml();
        assertEquals("<tfoot><tr><td>Total</td></tr></tfoot>", html);
    }
}

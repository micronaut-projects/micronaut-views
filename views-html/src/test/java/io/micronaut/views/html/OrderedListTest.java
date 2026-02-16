package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderedListTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(OrderedList.class));
    }

    @Test
    void toHtml() {
        String html = OrderedList.builder()
                .li(ListItem.builder().content("First").build())
                .li(ListItem.builder().content("Second").build())
                .build()
                .toHtml();
        assertEquals("<ol><li>First</li><li>Second</li></ol>", html);
    }
}

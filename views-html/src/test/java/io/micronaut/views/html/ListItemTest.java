package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ListItemTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(ListItem.class));
    }

    @Test
    void toHtml() {
        String html = ListItem.builder()
                .content("Item 1")
                .build()
                .toHtml();
        assertEquals("<li>Item 1</li>", html);
    }
}

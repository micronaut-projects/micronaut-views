package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UnorderedListTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(UnorderedList.class));
    }

    @Test
    void toHtml() {
        String html = UnorderedList.builder()
                .li(ListItem.builder().content("Apple").build())
                .li(ListItem.builder().content("Banana").build())
                .build()
                .toHtml();
        assertEquals("<ul><li>Apple</li><li>Banana</li></ul>", html);
    }
}

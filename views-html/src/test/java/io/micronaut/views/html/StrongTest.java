package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StrongTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Strong.class));
    }

    @Test
    void toHtml() {
        String html = Strong.builder()
                .content("important")
                .build()
                .toHtml();
        assertEquals("<strong>important</strong>", html);
    }
}

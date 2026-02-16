package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Main.class));
    }

    @Test
    void toHtml() {
        String html = Main.builder()
                .classAttribute("content")
                .content("Main content")
                .build()
                .toHtml();
        assertEquals("<main class=\"content\">Main content</main>", html);
    }
}

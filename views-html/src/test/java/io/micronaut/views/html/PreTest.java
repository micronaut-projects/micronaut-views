package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PreTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Pre.class));
    }

    @Test
    void toHtml() {
        String html = Pre.builder()
                .content("preformatted text")
                .build()
                .toHtml();
        assertEquals("<pre>preformatted text</pre>", html);
    }
}

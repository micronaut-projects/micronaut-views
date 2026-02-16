package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SpanTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Span.class));
    }

    @Test
    void toHtml() {
        String html = Span.builder()
                .classAttribute("badge")
                .content("New")
                .build()
                .toHtml();
        assertEquals("<span class=\"badge\">New</span>", html);
    }
}

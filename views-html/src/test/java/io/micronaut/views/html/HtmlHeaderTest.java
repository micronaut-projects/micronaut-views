package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HtmlHeaderTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(HtmlHeader.class));
    }

    @Test
    void toHtml() {
        String html = HtmlHeader.builder()
                .classAttribute("site-header")
                .content("Header content")
                .build()
                .toHtml();
        assertEquals("<header class=\"site-header\">Header content</header>", html);
    }
}

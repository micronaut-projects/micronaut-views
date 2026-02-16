package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FooterTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Footer.class));
    }

    @Test
    void toHtml() {
        String html = Footer.builder()
                .classAttribute("site-footer")
                .content("Footer content")
                .build()
                .toHtml();
        assertEquals("<footer class=\"site-footer\">Footer content</footer>", html);
    }
}

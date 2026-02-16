package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ParagraphTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Paragraph.class));
    }

    @Test
    void toHtml() {
        String html = Paragraph.builder()
                .content("Hello World")
                .build()
                .toHtml();
        assertEquals("<p>Hello World</p>", html);
    }

    @Test
    void toHtmlWithClass() {
        String html = Paragraph.builder()
                .classAttribute("lead")
                .content("Introduction")
                .build()
                .toHtml();
        assertEquals("<p class=\"lead\">Introduction</p>", html);
    }
}

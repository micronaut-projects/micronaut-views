package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockquoteTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Blockquote.class));
    }

    @Test
    void toHtml() {
        String html = Blockquote.builder()
                .classAttribute("blockquote")
                .content("A quote")
                .build()
                .toHtml();
        assertEquals("<blockquote class=\"blockquote\">A quote</blockquote>", html);
    }
}

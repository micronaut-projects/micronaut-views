package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DivTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Div.class));
    }

    @Test
    void toHtml() {
        String html = Div.builder()
                .classAttribute("container")
                .content("Hello")
                .build()
                .toHtml();
        assertEquals("<div class=\"container\">Hello</div>", html);
    }
}

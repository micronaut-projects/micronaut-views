package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OptionTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Option.class));
    }

    @Test
    void toHtml() {
        String html = Option.builder()
                .value("1")
                .content("Option 1")
                .build()
                .toHtml();
        assertEquals("<option value=\"1\">Option 1</option>", html);
    }

    @Test
    void toHtmlSelected() {
        String html = Option.builder()
                .value("2")
                .selected()
                .content("Option 2")
                .build()
                .toHtml();
        assertEquals("<option value=\"2\" selected=\"selected\">Option 2</option>", html);
    }
}

package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ButtonTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Button.class));
    }

    @Test
    void toHtml() {
        String html = Button.builder()
                .type("submit")
                .content("Submit")
                .build()
                .toHtml();
        assertEquals("<button type=\"submit\">Submit</button>", html);
    }

    @Test
    void toHtmlDisabled() {
        String html = Button.builder()
                .disabled()
                .content("Disabled")
                .build()
                .toHtml();
        assertEquals("<button disabled=\"disabled\">Disabled</button>", html);
    }

    @Test
    void toHtmlContainsAttributes() {
        String html = Button.builder()
                .type("button")
                .disabled()
                .content("Click")
                .build()
                .toHtml();
        assertTrue(html.startsWith("<button "));
        assertTrue(html.endsWith(">Click</button>"));
        assertTrue(html.contains("type=\"button\""));
        assertTrue(html.contains("disabled=\"disabled\""));
    }
}

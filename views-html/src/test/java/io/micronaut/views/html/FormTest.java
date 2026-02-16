package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Form.class));
    }

    @Test
    void toHtml() {
        String html = Form.builder()
                .action("/submit")
                .content("form content")
                .build()
                .toHtml();
        assertEquals("<form action=\"/submit\">form content</form>", html);
    }

    @Test
    void toHtmlContainsAttributes() {
        String html = Form.builder()
                .action("/submit")
                .method("post")
                .content("form content")
                .build()
                .toHtml();
        assertTrue(html.startsWith("<form "));
        assertTrue(html.endsWith(">form content</form>"));
        assertTrue(html.contains("action=\"/submit\""));
        assertTrue(html.contains("method=\"post\""));
    }
}

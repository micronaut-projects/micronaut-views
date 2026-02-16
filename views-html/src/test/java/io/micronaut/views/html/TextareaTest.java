package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TextareaTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Textarea.class));
    }

    @Test
    void toHtml() {
        String html = Textarea.builder()
                .name("comments")
                .rows("5")
                .cols("40")
                .content("Default text")
                .build()
                .toHtml();
        assertEquals("<textarea name=\"comments\" rows=\"5\" cols=\"40\">Default text</textarea>", html);
    }
}

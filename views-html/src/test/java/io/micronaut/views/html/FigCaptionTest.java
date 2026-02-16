package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FigCaptionTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(FigCaption.class));
    }

    @Test
    void toHtml() {
        String html = FigCaption.builder()
                .content("Figure caption")
                .build()
                .toHtml();
        assertEquals("<figcaption>Figure caption</figcaption>", html);
    }
}

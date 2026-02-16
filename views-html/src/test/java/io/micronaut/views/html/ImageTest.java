package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ImageTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Image.class));
    }

    @Test
    void toHtml() {
        String html = Image.builder()
                .src("/images/logo.png")
                .alt("Logo")
                .build()
                .toHtml();
        assertEquals("<img src=\"/images/logo.png\" alt=\"Logo\"/>", html);
    }

    @Test
    void toHtmlWithDimensions() {
        String html = Image.builder()
                .src("/images/photo.jpg")
                .alt("Photo")
                .width("200")
                .height("100")
                .build()
                .toHtml();
        assertEquals("<img src=\"/images/photo.jpg\" alt=\"Photo\" width=\"200\" height=\"100\"/>", html);
    }
}

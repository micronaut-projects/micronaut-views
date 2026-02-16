package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FigureTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Figure.class));
    }

    @Test
    void toHtml() {
        String html = Figure.builder()
                .element(Image.builder().src("/img.png").alt("image").build())
                .element(FigCaption.builder().content("A caption").build())
                .build()
                .toHtml();
        assertEquals("<figure><img src=\"/img.png\" alt=\"image\"/><figcaption>A caption</figcaption></figure>", html);
    }
}

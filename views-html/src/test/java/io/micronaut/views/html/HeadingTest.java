package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeadingTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Heading.class));
    }

    @Test
    void toHtmlDefaultLevel() {
        String html = Heading.builder()
                .content("Title")
                .build()
                .toHtml();
        assertEquals("<h1>Title</h1>", html);
    }

    @Test
    void toHtmlLevel2() {
        String html = Heading.builder()
                .level(2)
                .content("Subtitle")
                .build()
                .toHtml();
        assertEquals("<h2>Subtitle</h2>", html);
    }

    @Test
    void toHtmlLevel6() {
        String html = Heading.builder()
                .level(6)
                .content("Small heading")
                .build()
                .toHtml();
        assertEquals("<h6>Small heading</h6>", html);
    }

    @Test
    void invalidLevelThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                Heading.builder().level(0).content("Invalid").build());
        assertThrows(IllegalArgumentException.class, () ->
                Heading.builder().level(7).content("Invalid").build());
    }
}

package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AnchorTest {
    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Anchor.class));
    }

    @Test
    void aBuild() {
        assertEquals("""
                <a href="https://www.w3schools.com">Visit W3Schools.com!</a>""", Anchor.builder()
                .href("https://www.w3schools.com")
                .content("Visit W3Schools.com!")
                .build()
                .toHtml());
    }

    @Test
    void aBuildAttribute() {
        assertEquals("""
                <a href="https://www.w3schools.com">Visit W3Schools.com!</a>""", Anchor.builder()
                .attribute("href", "https://www.w3schools.com")
                .content("Visit W3Schools.com!")
                .build()
                .toHtml());
    }

}

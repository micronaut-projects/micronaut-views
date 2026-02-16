package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NavTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Nav.class));
    }

    @Test
    void toHtml() {
        String html = Nav.builder()
                .attribute("aria-label", "Main")
                .content("Navigation")
                .build()
                .toHtml();
        assertEquals("<nav aria-label=\"Main\">Navigation</nav>", html);
    }
}

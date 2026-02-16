package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AsideTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Aside.class));
    }

    @Test
    void toHtml() {
        String html = Aside.builder()
                .classAttribute("sidebar")
                .content("Sidebar content")
                .build()
                .toHtml();
        assertEquals("<aside class=\"sidebar\">Sidebar content</aside>", html);
    }
}

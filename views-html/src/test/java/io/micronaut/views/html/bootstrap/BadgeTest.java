package io.micronaut.views.html.bootstrap;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BadgeTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Badge.class));
    }

    @Test
    void toHtml() {
        String html = Badge.builder()
                .danger("Danger")
                .rounded()
                .build()
                .toHtml();
        assertEquals("""
                <span class="badge rounded-pill text-bg-danger">Danger</span>""", html);
    }
}

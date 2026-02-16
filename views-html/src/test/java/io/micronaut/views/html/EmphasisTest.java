package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EmphasisTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Emphasis.class));
    }

    @Test
    void toHtml() {
        String html = Emphasis.builder()
                .content("emphasized")
                .build()
                .toHtml();
        assertEquals("<em>emphasized</em>", html);
    }
}

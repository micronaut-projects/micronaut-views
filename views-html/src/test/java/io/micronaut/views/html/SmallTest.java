package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SmallTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Small.class));
    }

    @Test
    void toHtml() {
        String html = Small.builder()
                .content("fine print")
                .build()
                .toHtml();
        assertEquals("<small>fine print</small>", html);
    }
}

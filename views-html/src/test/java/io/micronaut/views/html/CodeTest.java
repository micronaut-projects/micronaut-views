package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CodeTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Code.class));
    }

    @Test
    void toHtml() {
        String html = Code.builder()
                .content("System.out.println()")
                .build()
                .toHtml();
        assertEquals("<code>System.out.println()</code>", html);
    }
}

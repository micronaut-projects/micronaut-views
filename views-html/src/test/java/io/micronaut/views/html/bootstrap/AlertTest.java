package io.micronaut.views.html.bootstrap;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Alert.class));
    }

    @Test
    void toHtml() {
        String html = Alert.builder()
                .danger("A simple danger alert—check it out!")
                .build()
                .toHtml();
        assertEquals("""
                <div role="alert" class="alert alert-danger">A simple danger alert—check it out!</div>""", html);
    }
}

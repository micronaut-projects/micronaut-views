package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LabelTest {
    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Label.class));
    }

    @Test
    void labelBuildHtml() {
        assertEquals("""
                <label for="html">HTML</label>""", Label.builder()
                .forAttribute("html")
                .content("HTML")
                .build()
                .toHtml());
    }

}

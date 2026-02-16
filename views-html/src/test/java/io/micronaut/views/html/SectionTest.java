package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SectionTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Section.class));
    }

    @Test
    void toHtml() {
        String html = Section.builder()
                .id("intro")
                .content("Introduction")
                .build()
                .toHtml();
        assertEquals("<section id=\"intro\">Introduction</section>", html);
    }
}

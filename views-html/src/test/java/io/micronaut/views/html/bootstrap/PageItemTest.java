package io.micronaut.views.html.bootstrap;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PageItemTest {
    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(PageItem.class));
    }

    @Test
    void toHtml() {
        String html = PageItem.builder()
                .text("1")
                .href("#")
                .build()
                .toHtml();
        String expected = """
                <li class="page-item"><a href="#" class="page-link">1</a></li>""";
        assertEquals(expected, html);
    }
}

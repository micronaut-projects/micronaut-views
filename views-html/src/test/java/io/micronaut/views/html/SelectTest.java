package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SelectTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Select.class));
    }

    @Test
    void toHtml() {
        String html = Select.builder()
                .name("color")
                .option(Option.builder().value("red").content("Red").build())
                .option(Option.builder().value("blue").content("Blue").build())
                .build()
                .toHtml();
        assertEquals("<select name=\"color\"><option value=\"red\">Red</option><option value=\"blue\">Blue</option></select>", html);
    }
}

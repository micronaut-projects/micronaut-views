package io.micronaut.views.fields.render;

import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.views.fields.HtmlAttribute;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HtmlAttributeTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(HtmlAttribute.class));
    }

    @Test
    void toStringRendersTheAttribute() {
        assertEquals("checked", new HtmlAttribute("checked", null).toString());
        assertEquals("id=\"foo\"", new HtmlAttribute("id", "foo").toString());
    }
}

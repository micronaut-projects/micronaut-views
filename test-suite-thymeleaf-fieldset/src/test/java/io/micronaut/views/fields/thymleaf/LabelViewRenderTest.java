package io.micronaut.views.fields.thymleaf;

import io.micronaut.core.io.Writable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.FormElement;
import io.micronaut.views.fields.InputHiddenFormElement;
import io.micronaut.views.fields.Message;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest(startApplication = false)
class LabelViewRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer) throws IOException {
        assertNotNull(viewsRenderer);
        Message message = Message.of("Foo Bar", "foo.bar");
        String id = "identifier";
        String expected = "<label for=\"identifier\" class=\"form-label\">Foo Bar</label>";
        assertEquals(expected, render(viewsRenderer, id, message));

        message = Message.of("Foo Bar", null);
        assertEquals(expected, render(viewsRenderer, id, message));

        expected = "<label class=\"form-label\">Foo Bar</label>";
        message = Message.of("Foo Bar", null);
        assertEquals(expected, render(viewsRenderer, null, message));

        message = Message.of("Foo Bar", "foo.bar");
        assertEquals(expected, render(viewsRenderer, null, message));
    }

    private static String render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer, String id, Message el) throws IOException {
        return output(viewsRenderer.render("fieldset/label.html", id != null ? Map.of("id", id, "el", el) : Collections.singletonMap("el", el), null));
    }

    private static String output(Writable writeable) throws IOException {
        StringWriter sw = new StringWriter();
        writeable.writeTo(sw);
        return sw.toString();
    }
}

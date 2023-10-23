package io.micronaut.views.fields.thymleaf;

import io.micronaut.core.io.Writable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.FormElement;
import io.micronaut.views.fields.InputHiddenFormElement;
import io.micronaut.views.fields.InputTimeFormElement;
import io.micronaut.views.fields.Message;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest(startApplication = false)
class InputTimeViewRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer) throws IOException {
        assertNotNull(viewsRenderer);
        InputTimeFormElement el = InputTimeFormElement.builder()
                .label(Message.of("Choose a time for your appointment:", null))
                .id("meeting-time")
                .name("meeting-time")
                .min(LocalTime.of(9, 0))
                .max(LocalTime.of(18, 0))
                .value(LocalTime.of(10, 0))
                .build();
        assertEquals("""
    <label for="meeting-time" class="form-label">Choose a time for your appointment:</label>\
    <input type="time" name="meeting-time" value="10:00" id="meeting-time" min="09:00" max="18:00" class="form-control"/>""", render(viewsRenderer, el));
    }

    private static String render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer, FormElement el) throws IOException {
        return output(viewsRenderer.render("fieldset/inputtime.html", Collections.singletonMap("el", el), null));
    }

    private static String output(Writable writeable) throws IOException {
        StringWriter sw = new StringWriter();
        writeable.writeTo(sw);
        return sw.toString();
    }
}

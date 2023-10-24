package io.micronaut.views.fields.thymleaf;

import io.micronaut.core.io.Writable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.FormElement;
import io.micronaut.views.fields.InputHiddenFormElement;
import io.micronaut.views.fields.InputTextFormElement;
import io.micronaut.views.fields.Message;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest(startApplication = false)
class InputStringViewRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer) throws IOException {
        assertNotNull(viewsRenderer);

        InputTextFormElement el = InputTextFormElement.builder()
                .name("name")
                .id("name")
                .minLength(4)
                .maxLength(8)
                .size(10)
                .required(true)
                .label(Message.of("Name (4 to 8 characters):", null))
                .build();
        assertEquals("""
            <label for="name" class="form-label">Name (4 to 8 characters):</label>\
            <input type="text" name="name" value="" id="name" minlength="4" maxlength="8" size="10" class="form-control" required="required"/>""",
                render(viewsRenderer, "text", el)
        );
    }

    private static String render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer, String type, FormElement el) throws IOException {
        return output(viewsRenderer.render("fieldset/inputstring.html", Map.of("type", type, "el", el), null));
    }

    private static String output(Writable writeable) throws IOException {
        StringWriter sw = new StringWriter();
        writeable.writeTo(sw);
        return sw.toString();
    }
}

package io.micronaut.views.fields.thymleaf;

import io.micronaut.core.io.Writable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest(startApplication = false)
class FormViewRenderTest {
    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer) throws IOException {
        assertNotNull(viewsRenderer);

        InputTextFormElement textElement = InputTextFormElement.builder()
                .name("name")
                .id("name")
                .minLength(4)
                .maxLength(8)
                .size(10)
                .required(true)
                .label(Message.of("Name (4 to 8 characters):", null))
                .build();
        Message value = Message.of("Send Request", null);
        InputSubmitFormElement el= InputSubmitFormElement.builder()
                .value(value)
                .build();
        Fieldset fieldset = new Fieldset(List.of(textElement, el), Collections.emptyList());
        Form form = new Form("/foo/bar", "post", fieldset);
        assertEquals("""
            <form action="/foo/bar" method="post">\
            <div class="mb-3">\
            <label for="name" class="form-label">Name (4 to 8 characters):</label>\
            <input type="text" name="name" value="" id="name" minlength="4" maxlength="8" size="10" class="form-control" required="required"/>\
            </div>\
            <input type="submit" value="Send Request" class="btn btn-primary"/></form>""",
                render(viewsRenderer, form)
        );
    }

    private static String render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer, Form el) throws IOException {
        return output(viewsRenderer.render("fieldset/form.html", Map.of("el", el), null));
    }

    private static String output(Writable writeable) throws IOException {
        StringWriter sw = new StringWriter();
        writeable.writeTo(sw);
        return sw.toString();
    }
}

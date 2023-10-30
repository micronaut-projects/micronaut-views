package io.micronaut.views.fields.tck;

import io.micronaut.core.io.Writable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest(startApplication = false)
class FieldsetViewRenderTest {

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

        Fieldset fieldset = new Fieldset(Collections.singletonList(textElement), Collections.emptyList());
        assertEquals("""
            <div class="mb-3">\
            <label for="name" class="form-label">Name (4 to 8 characters):</label>\
            <input type="text" name="name" value="" id="name" minlength="4" maxlength="8" size="10" class="form-control" required="required"/>\
            </div>""",
                render(viewsRenderer, fieldset)
        );


        InputHiddenFormElement el = InputHiddenFormElement.builder()
                .name("postId")
                .value("34657")
                .build();

        fieldset = new Fieldset(Arrays.asList(el, textElement), Collections.emptyList());
        assertEquals("""
            <input type="hidden" name="postId" value="34657"/>\
            <div class="mb-3">\
            <label for="name" class="form-label">Name (4 to 8 characters):</label>\
            <input type="text" name="name" value="" id="name" minlength="4" maxlength="8" size="10" class="form-control" required="required"/>\
            </div>""",
                render(viewsRenderer, fieldset)
        );
    }

    private static String render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer, Fieldset el) throws IOException {
        return output(viewsRenderer.render("fieldset/fieldset.html", Map.of("el", el), null));
    }

    private static String output(Writable writeable) throws IOException {
        StringWriter sw = new StringWriter();
        writeable.writeTo(sw);
        return sw.toString();
    }
}

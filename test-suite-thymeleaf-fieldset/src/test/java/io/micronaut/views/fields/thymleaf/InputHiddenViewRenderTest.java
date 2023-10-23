package io.micronaut.views.fields.thymleaf;

import io.micronaut.core.io.Writable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.FormElement;
import io.micronaut.views.fields.InputHiddenFormElement;
import io.micronaut.views.fields.annotations.InputHidden;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest(startApplication = false)
class InputHiddenViewRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer) throws IOException {
        assertNotNull(viewsRenderer);
        InputHiddenFormElement el = InputHiddenFormElement.builder()
            .name("postId")
            .value("34657")
            .build();
        assertEquals("""
        <input type="hidden" name="postId" value="34657"/>""", render(viewsRenderer, el));
    }

    private static String render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer, FormElement el) throws IOException {
        return output(viewsRenderer.render("fieldset/inputhidden.html", Collections.singletonMap("el", el), null));
    }

    private static String output(Writable writeable) throws IOException {
        StringWriter sw = new StringWriter();
        writeable.writeTo(sw);
        return sw.toString();
    }
}

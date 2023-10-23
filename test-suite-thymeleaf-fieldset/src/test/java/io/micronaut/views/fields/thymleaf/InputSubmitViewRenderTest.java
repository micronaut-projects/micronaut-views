package io.micronaut.views.fields.thymleaf;

import io.micronaut.core.io.Writable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.FormElement;
import io.micronaut.views.fields.InputHiddenFormElement;
import io.micronaut.views.fields.InputSubmitFormElement;
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
class InputSubmitViewRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer) throws IOException {
        assertNotNull(viewsRenderer);
        Message value = Message.of("Send Request", null);
        InputSubmitFormElement el= InputSubmitFormElement.builder()
                .value(value)
                .build();
        String expected = """
            <input type="submit" value="Send Request" class="btn btn-primary"/>""";
        assertEquals(expected, render(viewsRenderer, el));

        value = Message.of("Send Request", "foobar");
        el= InputSubmitFormElement.builder()
                .value(value)
                .build();
        assertEquals(expected, render(viewsRenderer, el));
    }

    private static String render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer, FormElement el) throws IOException {
        return output(viewsRenderer.render("fieldset/inputsubmit.html", Collections.singletonMap("el", el), null));
    }

    private static String output(Writable writeable) throws IOException {
        StringWriter sw = new StringWriter();
        writeable.writeTo(sw);
        return sw.toString();
    }
}

package io.micronaut.views.fields.thymleaf;

import io.micronaut.core.io.Writable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.Message;
import io.micronaut.views.fields.Option;
import io.micronaut.views.fields.SimpleMessage;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest(startApplication = false)
class OptionViewRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer) throws IOException {
        Option option = Option.builder()
                .value("dog")
                .label(new SimpleMessage("Dog", null))
                .build();
        String expected = "<option value=\"dog\">Dog</option>";
        assertEquals(expected, render(viewsRenderer, option));

        option = Option.builder()
                .value("dog")
                .label(new SimpleMessage("Dog", "foobar"))
                .build();
        assertEquals(expected, render(viewsRenderer, option));
    }

    private static String render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer, Option el) throws IOException {
        return output(viewsRenderer.render("fieldset/option.html", Collections.singletonMap("el", el), null));
    }

    private static String output(Writable writeable) throws IOException {
        StringWriter sw = new StringWriter();
        writeable.writeTo(sw);
        return sw.toString();
    }
}

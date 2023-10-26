package com.projectcheckins.views;

import com.projectcheckins.services.AnswerRow;
import io.micronaut.core.io.Writable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest(startApplication = false)
class AnswerUnderscoreShowViewRenderTest {
    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer) throws IOException {
        assertNotNull(viewsRenderer);
        AnswerRow el = new AnswerRow("foo <b>bar</b>");
        assertEquals("""
        <div>foo <b>bar</b></div>""", render(viewsRenderer, el));
    }

    private static String render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer, AnswerRow el) throws IOException {
        return output(viewsRenderer.render("answer/_show.html", Collections.singletonMap("answer", el), null));
    }

    private static String output(Writable writeable) throws IOException {
        StringWriter sw = new StringWriter();
        writeable.writeTo(sw);
        return sw.toString();
    }
}

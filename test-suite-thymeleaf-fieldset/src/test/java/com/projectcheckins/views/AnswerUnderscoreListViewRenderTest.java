package com.projectcheckins.views;

import com.projectcheckins.services.AnswerRow;
import io.micronaut.core.io.Writable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest(startApplication = false)
class AnswerUnderscoreListViewRenderTest {
    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer) throws IOException {
        assertNotNull(viewsRenderer);
        List<AnswerRow> el = Collections.emptyList();
        assertEquals("""
        <div id="answers"></div>""", render(viewsRenderer, el));

        el = Collections.singletonList(new AnswerRow("foo <b>bar</b>"));
        assertEquals("""
        <div id="answers"><div>foo <b>bar</b></div></div>""", render(viewsRenderer, el));
    }

    private static String render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer, List<AnswerRow> answers) throws IOException {
        return output(viewsRenderer.render("answer/_list.html", Collections.singletonMap("answers", answers), null));
    }

    private static String output(Writable writeable) throws IOException {
        StringWriter sw = new StringWriter();
        writeable.writeTo(sw);
        return sw.toString();
    }
}

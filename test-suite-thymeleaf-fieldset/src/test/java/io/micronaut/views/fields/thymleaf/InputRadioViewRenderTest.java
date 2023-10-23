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

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class InputRadioViewRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer) throws IOException {
        assertNotNull(viewsRenderer);

        Radio huey = Radio.builder()
                .id("huey")
                .value("huey")
                .checked(true)
                .label(Message.of("Huey", null))
                .build();
        InputRadioFormElement el = InputRadioFormElement.builder()
                .name("drone")
                .buttons(List.of(
                        huey,
                        Radio.builder()
                                .id("dewey")
                                .value("dewey")
                                .label(Message.of("Dewey", null))
                                .build(),
                        Radio.builder()
                                .id("louie")
                                .value("louie")
                                .label(Message.of("Louie", null))
                                .build()
                ))
                .build();
        String html = render(viewsRenderer, el, huey);
        assertTrue("<input type=\"radio\" name=\"drone\" value=\"huey\" id=\"huey\" class=\"form-check-input\" checked/>".equals(html) ||
                            "<input type=\"radio\" name=\"drone\" value=\"huey\" id=\"huey\" class=\"form-check-input\" checked=\"checked\"/>".equals(html));

        html = render(viewsRenderer, el);
        assertEquals("""
    <div class="form-check"><input type="radio" name="drone" value="huey" id="huey" class="form-check-input" checked="checked"/><label for="huey" class="form-label">Huey</label></div>\
    <div class="form-check"><input type="radio" name="drone" value="dewey" id="dewey" class="form-check-input"/><label for="dewey" class="form-label">Dewey</label></div>\
    <div class="form-check"><input type="radio" name="drone" value="louie" id="louie" class="form-check-input"/><label for="louie" class="form-label">Louie</label></div>""", html);
    }

    private static String render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer, InputRadioFormElement el, Radio radio) throws IOException {
        return output(viewsRenderer.render("fieldset/inputradio.html", Map.of( "el", el, "radio", radio), null));
    }

    private static String render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer, InputRadioFormElement el) throws IOException {
        return output(viewsRenderer.render("fieldset/inputradios.html", Collections.singletonMap( "el", el), null));
    }

    private static String output(Writable writeable) throws IOException {
        StringWriter sw = new StringWriter();
        writeable.writeTo(sw);
        return sw.toString();
    }
}

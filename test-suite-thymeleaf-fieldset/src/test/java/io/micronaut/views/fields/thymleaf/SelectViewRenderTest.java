package io.micronaut.views.fields.thymleaf;

import io.micronaut.core.io.Writable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.FormElement;
import io.micronaut.views.fields.Message;
import io.micronaut.views.fields.Option;
import io.micronaut.views.fields.SelectFormElement;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest(startApplication = false)
class SelectViewRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer) throws IOException {
        SelectFormElement el = SelectFormElement.builder()
                .label(Message.of("Choose a pet:", null))
                .id("pet-select")
                .name("pets")
                .options(List.of(
                        Option.builder()
                                .value("dog")
                                .label(Message.of("Dog", null))
                                .build(),
                        Option.builder()
                                .value("cat")
                                .label(Message.of("Cat", null))
                                .build(),
                        Option.builder()
                                .value("hamster")
                                .label(Message.of("Hamster", null))
                                .build(),
                        Option.builder()
                                .value("parrot")
                                .label(Message.of("Parrot", null))
                                .build(),
                        Option.builder()
                                .value("spider")
                                .label(Message.of("Spider", null))
                                .build(),
                        Option.builder()
                                .value("goldfish")
                                .label(Message.of("Goldfish", null))
                                .build()
                ))
                .build();
        assertEquals("""
            <label for="pet-select" class="form-label">Choose a pet:</label><select name="pets" id="pet-select">\
            <option value="dog">Dog</option>\
            <option value="cat">Cat</option>\
            <option value="hamster">Hamster</option>\
            <option value="parrot">Parrot</option>\
            <option value="spider">Spider</option>\
            <option value="goldfish">Goldfish</option>\
            </select>""",
                render(viewsRenderer, el)
        );
    }

    private static String render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer, FormElement el) throws IOException {
        return output(viewsRenderer.render("fieldset/select.html", Collections.singletonMap("el", el), null));
    }

    private static String output(Writable writeable) throws IOException {
        StringWriter sw = new StringWriter();
        writeable.writeTo(sw);
        return sw.toString();
    }
}

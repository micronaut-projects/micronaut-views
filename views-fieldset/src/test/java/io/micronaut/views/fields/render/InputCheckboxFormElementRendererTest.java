package io.micronaut.views.fields.render;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.elements.Checkbox;
import io.micronaut.views.fields.elements.InputCheckboxFormElement;
import io.micronaut.views.fields.messages.Message;
import io.micronaut.views.fields.render.secondary.InputCheckboxFormElementRenderer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest(startApplication = false)
class InputCheckboxFormElementRendererTest {

    @Test
    void render(InputCheckboxFormElementRenderer renderer) {
        InputCheckboxFormElement el = InputCheckboxFormElement.builder()
            .checkboxes(List.of(
                Checkbox.builder().id("scales").name("scales").label(Message.of("Scales")).checked(true).build(),
                Checkbox.builder().id("horns").name("horns").label(Message.of("Horns")).build()
            ))
            .build();
        assertEquals("""
            <div><input type="checkbox" name="scales" id="scales" checked/><label for="scales">Scales</label></div>\
            <div><input type="checkbox" name="horns" id="horns"/><label for="horns">Horns</label></div>""",
            renderer.render(el, Locale.ENGLISH)
        );
    }
}

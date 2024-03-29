package io.micronaut.views.fields.render;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.elements.InputNumberFormElement;
import io.micronaut.views.fields.messages.Message;
import io.micronaut.views.fields.render.secondary.InputNumberFormElementRenderer;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest(startApplication = false)
class InputNumberFormElementRendererTest {

    @Test
    void render(InputNumberFormElementRenderer renderer) {
        InputNumberFormElement el = InputNumberFormElement.builder()
            .label(Message.of("Number of tentacles (10-100):"))
            .id("tentacles")
            .name("tentacles")
            .min(10)
            .max(100)
            .build();
        assertEquals("""
            <label for="tentacles">Number of tentacles (10-100):</label><input type="number" name="tentacles" id="tentacles" min="10" max="100"/>""",
            renderer.render(el, Locale.ENGLISH)
        );
    }
}

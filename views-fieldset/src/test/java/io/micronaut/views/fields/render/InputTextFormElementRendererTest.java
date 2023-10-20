package io.micronaut.views.fields.render;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.InputTextFormElement;
import io.micronaut.views.fields.Message;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest(startApplication = false)
class InputTextFormElementRendererTest {

    @Test
    void render(InputTextFormElementRenderer renderer) {
        InputTextFormElement el = InputTextFormElement.builder()
            .name("name")
            .id("name")
            .minLength(4)
            .maxLength(8)
            .size(10)
            .required(true)
            .label(Message.of("Name (4 to 8 characters):", null))
            .build();
        assertEquals("""
            <label for="name">Name (4 to 8 characters):</label><input type="text" name="name" id="name" minlength="4" maxlength="8" size="10" required/>""",
            renderer.render(el, Locale.ENGLISH)
        );
    }
}

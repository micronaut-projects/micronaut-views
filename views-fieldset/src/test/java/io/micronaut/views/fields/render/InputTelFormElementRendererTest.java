package io.micronaut.views.fields.render;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.InputTelFormElement;
import io.micronaut.views.fields.InputUrlFormElement;
import io.micronaut.views.fields.Message;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@MicronautTest(startApplication = false)
class InputTelFormElementRendererTest {

    @Test
    void render(InputTelFormElementRenderer renderer) {
        InputTelFormElement el = InputTelFormElement.builder()
            .name("phone")
            .id("phone")
            .required(true)
            .pattern("[0-9]{3}-[0-9]{3}-[0-9]{4}")
            .label(Message.of("Enter your phone number:", null))
            .build();
        assertEquals("""
            <label for="phone">Enter your phone number:</label><input type="tel" name="phone" id="phone" pattern="[0-9]{3}-[0-9]{3}-[0-9]{4}" required/>""",
            renderer.render(el, Locale.ENGLISH)


        );
        assertFalse(el.hasErrors());
    }
}

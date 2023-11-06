package io.micronaut.views.fields.render;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.InputPasswordFormElement;
import io.micronaut.views.fields.Message;
import io.micronaut.views.fields.render.secondary.InputPasswordFormElementRenderer;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest(startApplication = false)
class InputPasswordFormElementRendererTest {

    @Test
    void render(InputPasswordFormElementRenderer renderer) {
        InputPasswordFormElement el = InputPasswordFormElement.builder()
            .name("password")
            .id("pass")
            .minLength(8)
            .required(true)
            .label(Message.of("Password (8 characters minimum):"))
            .build();
        assertEquals("""
            <label for="pass">Password (8 characters minimum):</label><input type="password" name="password" id="pass" minlength="8" required/>""",
            renderer.render(el, Locale.ENGLISH)
        );
    }
}

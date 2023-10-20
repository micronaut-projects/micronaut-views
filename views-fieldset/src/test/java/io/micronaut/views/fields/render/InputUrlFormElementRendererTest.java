package io.micronaut.views.fields.render;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.InputUrlFormElement;
import io.micronaut.views.fields.Message;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class InputUrlFormElementRendererTest {

    @Test
    void render(InputUrlFormElementRenderer renderer) {
        InputUrlFormElement el = InputUrlFormElement.builder()
            .name("url")
            .id("url")
            .placeholder("https://example.com")
            .pattern("https://.*")
            .size(30)
            .required(true)
            .label(Message.of("Enter an https:// URL:", null))
            .build();
        assertEquals("""
            <label for="url">Enter an https:// URL:</label><input type="url" name="url" id="url" placeholder="https://example.com" pattern="https://.*" size="30" required/>""",
            renderer.render(el, Locale.ENGLISH)
        );
        assertFalse(el.hasErrors());
    }
}

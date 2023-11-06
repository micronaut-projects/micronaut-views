package io.micronaut.views.fields.render;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.HtmlTag;
import io.micronaut.views.fields.InputType;
import io.micronaut.views.fields.InputUrlFormElement;
import io.micronaut.views.fields.Message;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@MicronautTest(startApplication = false)
class InputUrlFormElementRendererTest {

    @Test
    void testTagAndType() {
        InputUrlFormElement formElement = InputUrlFormElement.builder().build();
        assertEquals(HtmlTag.TAG_INPUT, formElement.getTag());
        assertEquals(InputType.ATTR_TYPE_URL, formElement.getType());
    }

    @Test
    void render(InputUrlFormElementRenderer renderer) {
        InputUrlFormElement el = InputUrlFormElement.builder()
            .name("url")
            .id("url")
            .placeholder("https://example.com")
            .pattern("https://.*")
            .size(30)
            .required(true)
            .label(Message.of("Enter an https:// URL:"))
            .build();
        assertEquals("""
            <label for="url">Enter an https:// URL:</label><input type="url" name="url" id="url" placeholder="https://example.com" pattern="https://.*" size="30" required/>""",
            renderer.render(el, Locale.ENGLISH)
        );
        assertFalse(el.hasErrors());
    }
}

package io.micronaut.views.fields.render;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.HtmlTag;
import io.micronaut.views.fields.elements.InputEmailFormElement;
import io.micronaut.views.fields.InputType;
import io.micronaut.views.fields.messages.Message;
import io.micronaut.views.fields.render.secondary.InputEmailFormElementRenderer;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@MicronautTest(startApplication = false)
class InputEmailFormElementRendererTest {
    @Test
    void testTagAndType() {
        InputEmailFormElement formElement = InputEmailFormElement.builder().build();
        assertEquals(HtmlTag.INPUT, formElement.getTag());
        assertEquals(InputType.EMAIL, formElement.getType());
    }

    @Test
    void render(InputEmailFormElementRenderer renderer) {
        InputEmailFormElement el = InputEmailFormElement.builder()
            .name("email")
            .id("email")
            .required(true)
            .pattern(".+@globex\\.com")
            .size(30)
            .label(Message.of("Enter your globex.com email:"))
            .build();
        assertEquals("""
            <label for="email">Enter your globex.com email:</label><input type="email" name="email" id="email" pattern=".+@globex\\.com" size="30" required/>""",
            renderer.render(el, Locale.ENGLISH)
        );
        assertFalse(el.hasErrors());
    }
}

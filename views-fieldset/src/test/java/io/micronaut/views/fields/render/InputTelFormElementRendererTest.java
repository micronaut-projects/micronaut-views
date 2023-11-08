package io.micronaut.views.fields.render;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.HtmlTag;
import io.micronaut.views.fields.elements.InputTelFormElement;
import io.micronaut.views.fields.InputType;
import io.micronaut.views.fields.messages.Message;
import io.micronaut.views.fields.render.secondary.InputTelFormElementRenderer;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@MicronautTest(startApplication = false)
class InputTelFormElementRendererTest {
    @Test
    void testTagAndType() {
        InputTelFormElement formElement = InputTelFormElement.builder().build();
        assertEquals(HtmlTag.INPUT, formElement.getTag());
        assertEquals(InputType.ATTR_TYPE_TEL, formElement.getType());
    }

    @Test
    void render(InputTelFormElementRenderer renderer) {
        InputTelFormElement el = InputTelFormElement.builder()
            .name("phone")
            .id("phone")
            .required(true)
            .pattern("[0-9]{3}-[0-9]{3}-[0-9]{4}")
            .label(Message.of("Enter your phone number:"))
            .build();
        assertEquals("""
            <label for="phone">Enter your phone number:</label><input type="tel" name="phone" id="phone" pattern="[0-9]{3}-[0-9]{3}-[0-9]{4}" required/>""",
            renderer.render(el, Locale.ENGLISH)
        );
        assertFalse(el.hasErrors());
    }
}

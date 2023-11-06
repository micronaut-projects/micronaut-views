package io.micronaut.views.fields.render;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.InputSubmitFormElement;
import io.micronaut.views.fields.Message;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest(startApplication = false)
class InputSubmitFormElementRenderTest {

    @Test
    void renderOption(InputSubmitFormElementRenderer renderer) {
        Message value = Message.of("Send Request");
        InputSubmitFormElement el= InputSubmitFormElement.builder()
            .value(value)
            .build();
        assertEquals("""
            <input type="submit" value="Send Request"/>""",
            renderer.render(el, Locale.ENGLISH)
        );
    }
}

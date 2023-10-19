package io.micronaut.views.fields.render;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class InputRadioFormElementRendererTest {
    @Test
    void renderOption(InputRadioFormElementRenderer inputRadioFormElementRenderer) {
        InputRadioFormElement inputRadioFormElement = InputRadioFormElement.builder()
            .name("drone")
            .buttons(List.of(
                Radio.builder()
                    .id("huey")
                    .value("huey")
                    .checked(true)
                    .label(Message.of("Huey", null))
                    .build(),
                Radio.builder()
                    .id("dewey")
                    .value("dewey")
                    .label(Message.of("Dewey", null))
                    .build(),
                Radio.builder()
                    .id("louie")
                    .value("louie")
                    .label(Message.of("Louie", null))
                    .build()
            ))
            .build();
        assertEquals("<div><input type=\"radio\" name=\"drone\" value=\"huey\" id=\"huey\" checked/><label for=\"huey\">Huey</label></div>" +
                "<div><input type=\"radio\" name=\"drone\" value=\"dewey\" id=\"dewey\"/><label for=\"dewey\">Dewey</label></div>" +
                "<div><input type=\"radio\" name=\"drone\" value=\"louie\" id=\"louie\"/><label for=\"louie\">Louie</label></div>", inputRadioFormElementRenderer.render(inputRadioFormElement, Locale.ENGLISH));
    }
}

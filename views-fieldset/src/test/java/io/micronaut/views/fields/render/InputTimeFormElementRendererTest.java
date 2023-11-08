package io.micronaut.views.fields.render;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.elements.InputTimeFormElement;
import io.micronaut.views.fields.messages.Message;
import io.micronaut.views.fields.render.secondary.InputTimeFormElementRenderer;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest(startApplication = false)
class InputTimeFormElementRendererTest {
    @Test
    void render(InputTimeFormElementRenderer renderer) {
        InputTimeFormElement el = InputTimeFormElement.builder()
            .label(Message.of("Choose a time for your appointment:"))
            .id("meeting-time")
            .name("meeting-time")
            .min(LocalTime.of(9, 0))
            .max(LocalTime.of(18, 0))
            .value(LocalTime.of(10, 0))
            .build();
        assertEquals("<label for=\"meeting-time\">Choose a time for your appointment:</label><input type=\"time\" name=\"meeting-time\" value=\"10:00\" id=\"meeting-time\" min=\"09:00\" max=\"18:00\"/>", renderer.render(el, Locale.ENGLISH));
    }
}

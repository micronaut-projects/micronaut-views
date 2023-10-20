package io.micronaut.views.fields.render;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.InputDateTimeLocalFormElement;
import io.micronaut.views.fields.Message;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest(startApplication = false)
class InputDateTimeFormElementRendererTest {
    @Test
    void render(InputDateTimeLocalFormElementRenderer renderer) {
        InputDateTimeLocalFormElement el = InputDateTimeLocalFormElement.builder()
            .label(Message.of("Choose a time for your appointment:", null))
            .id("meeting-time")
            .name("meeting-time")
            .min(LocalDateTime.of(2018, 6, 7, 0, 0))
            .max(LocalDateTime.of(2018, 6, 14, 0, 0))
            .value(LocalDateTime.of(2018, 6, 12, 19, 30))
            .build();
        assertEquals("""
            <label for="meeting-time">Choose a time for your appointment:</label><input type="datetime-local" name="meeting-time" value="2018-06-12T19:30" id="meeting-time" min="2018-06-07T00:00" max="2018-06-14T00:00"/>""",
            renderer.render(el, Locale.ENGLISH)
        );
    }
}

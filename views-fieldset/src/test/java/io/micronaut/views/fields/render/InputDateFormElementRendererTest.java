package io.micronaut.views.fields.render;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.InputDateFormElement;
import io.micronaut.views.fields.Message;
import io.micronaut.views.fields.render.secondary.InputDateFormElementRenderer;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest(startApplication = false)
class InputDateFormElementRendererTest {
    @Test
    void render(InputDateFormElementRenderer renderer) {
        InputDateFormElement el = InputDateFormElement.builder()
            .label(Message.of("Start date:"))
            .id("start")
            .name("trip-start")
            .min(LocalDate.of(2018, 1, 1))
            .max(LocalDate.of(2018, 12, 31))
            .value(LocalDate.of(2018, 7, 22))
            .build();
        assertEquals("""
            <label for="start">Start date:</label><input type="date" name="trip-start" value="2018-07-22" id="start" min="2018-01-01" max="2018-12-31"/>""",
            renderer.render(el, Locale.ENGLISH)
        );
    }
}

package io.micronaut.views.fields.tck;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.io.Writable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.*;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class InputTimeViewRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer) throws IOException {
        assertNotNull(viewsRenderer);
        InputTimeFormElement el = InputTimeFormElement.builder()
                .label(Message.of("Choose a time for your appointment:", null))
                .id("meeting-time")
                .name("meeting-time")
                .min(LocalTime.of(9, 0))
                .max(LocalTime.of(18, 0))
                .value(LocalTime.of(10, 0))
                .build();
        assertEquals("""
                <label for="meeting-time" class="form-label">Choose a time for your appointment:</label>\
                <input type="time" name="meeting-time" value="10:00" id="meeting-time" min="09:00" max="18:00" class="form-control"/>""", render(viewsRenderer, el));
    }

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer,
                FieldsetGenerator fieldsetGenerator,
                FormValidator formValidator) throws IOException {
        assertNotNull(viewsRenderer);

        Fieldset fieldset = fieldsetGenerator.generate(new Event(LocalTime.of(16, 30)));
        assertEquals("""
        <div class="mb-3"><label for="meetingTime" class="form-label">Meeting Time</label><input type="time" name="meetingTime" value="16:30" id="meetingTime" class="form-control" required="required"/></div>""", TestUtils.render("fieldset/fieldset.html", viewsRenderer, Map.of("el", fieldset)));


        Event invalid = new Event(null);
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> formValidator.validate(invalid));
        fieldset = fieldsetGenerator.generate(invalid, ex);
        assertEquals("""
                <div class="mb-3"><label for="meetingTime" class="form-label">Meeting Time</label><input type="time" name="meetingTime" value="" id="meetingTime" class="form-control is-invalid" aria-describedby="meetingTimeValidationServerFeedback" required="required"/><div id="meetingTimeValidationServerFeedback" class="invalid-feedback">must not be null</div></div>""", TestUtils.render("fieldset/fieldset.html", viewsRenderer, Map.of("el", fieldset)));
    }

    private static String render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer, FormElement el) throws IOException {
        return TestUtils.output(viewsRenderer.render("fieldset/inputtime.html", Collections.singletonMap("el", el), null));
    }

    @Introspected
    record Event(@NotNull LocalTime meetingTime) {

    }
}

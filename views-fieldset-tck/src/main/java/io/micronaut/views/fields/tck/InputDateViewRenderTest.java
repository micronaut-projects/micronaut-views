package io.micronaut.views.fields.tck;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.*;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class InputDateViewRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer,
                FieldsetGenerator fieldsetGenerator,
                FormValidator formValidator) throws IOException {
        assertNotNull(viewsRenderer);

        Fieldset fieldset = fieldsetGenerator.generate(new Event(LocalDate.of(2023, 10, 28)));
        assertEquals("""
        <div class="mb-3"><label for="meetingDate" class="form-label">Meeting Date</label><input type="date" name="meetingDate" value="2023-10-28" id="meetingDate" class="form-control" required="required"/></div>""", TestUtils.render("fieldset/fieldset.html", viewsRenderer, Map.of("el", fieldset)));


        Event invalid = new Event(null);
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> formValidator.validate(invalid));
        fieldset = fieldsetGenerator.generate(invalid, ex);
        assertEquals("""
                <div class="mb-3"><label for="meetingDate" class="form-label">Meeting Date</label><input type="date" name="meetingDate" value="" id="meetingDate" class="form-control is-invalid" aria-describedby="meetingDateValidationServerFeedback" required="required"/><div id="meetingDateValidationServerFeedback" class="invalid-feedback">must not be null</div></div>""", TestUtils.render("fieldset/fieldset.html", viewsRenderer, Map.of("el", fieldset)));
    }

    @Introspected
    record Event(@NotNull LocalDate meetingDate) {
    }
}

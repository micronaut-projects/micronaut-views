/*
 * Copyright 2017-2023 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.views.fields.tck;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.Fieldset;
import io.micronaut.views.fields.FieldsetGenerator;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest(startApplication = false)
@SuppressWarnings({"java:S5960"}) // Assertions are fine, these are tests
class InputDateTimeLocalViewRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer,
                FieldsetGenerator fieldsetGenerator,
                FormValidator formValidator) throws IOException {
        assertNotNull(viewsRenderer);

        Fieldset fieldset = fieldsetGenerator.generate(new Event(LocalDateTime.of(2023, 10, 28, 16, 30)));
        assertEquals("""
                <div class="mb-3">\
                <label for="meetingDate" class="form-label">Meeting Date</label>\
                <input type="datetime-local" name="meetingDate" value="2023-10-28T16:30" id="meetingDate" class="form-control" required="required"/>\
                </div>""",
            TestUtils.render("fieldset/fieldset.html", viewsRenderer, Map.of("el", fieldset))
        );

        @SuppressWarnings("java:S2637") // We're passing null on purpose
        Event invalid = new Event(null);
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> formValidator.validate(invalid));
        fieldset = fieldsetGenerator.generate(invalid, ex);
        assertEquals("""
                <div class="mb-3">\
                <label for="meetingDate" class="form-label">Meeting Date</label>\
                <input type="datetime-local" name="meetingDate" value="" id="meetingDate" class="form-control is-invalid" aria-describedby="meetingDateValidationServerFeedback" required="required"/>\
                <div id="meetingDateValidationServerFeedback" class="invalid-feedback">must not be null</div>\
                </div>""",
            TestUtils.render("fieldset/fieldset.html", viewsRenderer, Map.of("el", fieldset))
        );
    }

    @Introspected
    record Event(@NotNull LocalDateTime meetingDate) {
    }
}

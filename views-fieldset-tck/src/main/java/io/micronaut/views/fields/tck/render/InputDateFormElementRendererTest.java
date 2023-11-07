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
package io.micronaut.views.fields.tck.render;

import io.micronaut.context.annotation.Property;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.elements.InputDateFormElement;
import io.micronaut.views.fields.messages.Message;
import io.micronaut.views.fields.render.FormElementRenderer;
import io.micronaut.views.fields.render.secondary.InputDateFormElementRenderer;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@Property(name = "micronaut.views.form-element.render.views.input-date", value = "fieldset/inputdate.html")
@MicronautTest(startApplication = false)
@SuppressWarnings({
    "java:S5960", // Assertions are fine, these are tests
    "java:S6813"  // As are field injections
})
class InputDateFormElementRendererTest {

    @Inject
    FormElementRenderer<InputDateFormElement> renderer;

    @Test
    void render() {
        assertFalse(renderer instanceof InputDateFormElementRenderer);
        InputDateFormElement el = InputDateFormElement.builder()
            .label(Message.of("Start date:", null))
            .id("start")
            .name("trip-start")
            .min(LocalDate.of(2018, 1, 1))
            .max(LocalDate.of(2018, 12, 31))
            .value(LocalDate.of(2018, 7, 22))
            .build();
        assertEquals("""
            <label for="start" class="form-label">Start date:</label>\
            <input type="date" name="trip-start" value="2018-07-22" id="start" min="2018-01-01" max="2018-12-31" class="form-control"/>""",
            renderer.render(el, Locale.ENGLISH)
        );
    }
}

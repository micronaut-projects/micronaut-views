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
import io.micronaut.views.fields.elements.InputDateTimeLocalFormElement;
import io.micronaut.views.fields.message.Message;
import io.micronaut.views.fields.render.FormElementRenderer;
import io.micronaut.views.fields.render.secondary.InputDateTimeLocalFormElementRenderer;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Property(name = "micronaut.views.form-element.render.views.input-date-time-local", value = "fieldset/inputdatetimelocal.html")
@MicronautTest(startApplication = false)
@SuppressWarnings({
    "java:S5960", // Assertions are fine, these are tests
    "java:S6813"  // As are field injections
})
class InputDateTimeFormElementRendererTest {

    @Inject
    FormElementRenderer<InputDateTimeLocalFormElement> renderer;

    @Test
    void render() {
        assertFalse(renderer instanceof InputDateTimeLocalFormElementRenderer);
        InputDateTimeLocalFormElement el = InputDateTimeLocalFormElement.builder()
            .label(Message.of("Choose a time for your appointment:", null))
            .id("meeting-time")
            .name("meeting-time")
            .min(LocalDateTime.of(2018, 6, 7, 0, 0))
            .max(LocalDateTime.of(2018, 6, 14, 0, 0))
            .value(LocalDateTime.of(2018, 6, 12, 19, 30))
            .build();
        assertEquals("""
            <label for="meeting-time" class="form-label">Choose a time for your appointment:</label>\
            <input type="datetime-local" name="meeting-time" value="2018-06-12T19:30" id="meeting-time" min="2018-06-07T00:00" max="2018-06-14T00:00" class="form-control"/>""",
            renderer.render(el, Locale.ENGLISH)
        );
    }
}

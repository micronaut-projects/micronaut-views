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
import io.micronaut.views.fields.elements.InputTimeFormElement;
import io.micronaut.views.fields.messages.Message;
import io.micronaut.views.fields.render.FormElementRenderer;
import io.micronaut.views.fields.render.secondary.InputTimeFormElementRenderer;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Locale;

import static io.micronaut.views.fields.tck.AsssertHtmlUtils.assertHtmlEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Property(name = "micronaut.views.form-element.render.views.input-time", value = "fieldset/inputtime")
@MicronautTest(startApplication = false)
@SuppressWarnings({
    "java:S5960", // Assertions are fine, these are tests
    "java:S6813"  // As are field injections
})
class InputTimeFormElementRendererTest {

    @Inject
    FormElementRenderer<InputTimeFormElement> renderer;

    @Test
    void render() {
        assertFalse(renderer instanceof InputTimeFormElementRenderer);
        InputTimeFormElement el = InputTimeFormElement.builder()
            .label(Message.of("Choose a time for your appointment:", null))
            .id("meeting-time")
            .name("meeting-time")
            .min(LocalTime.of(9, 0))
            .max(LocalTime.of(18, 0))
            .value(LocalTime.of(10, 0))
            .build();
        assertHtmlEquals("""
                <label for="meeting-time" class="form-label">Choose a time for your appointment:</label>\
                <input type="time" name="meeting-time" value="10:00" id="meeting-time" min="09:00" max="18:00" class="form-control"/>""",
            renderer.render(el, Locale.ENGLISH)
        );
    }
}

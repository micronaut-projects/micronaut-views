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
import io.micronaut.views.fields.elements.InputNumberFormElement;
import io.micronaut.views.fields.messages.Message;
import io.micronaut.views.fields.render.FormElementRenderer;
import io.micronaut.views.fields.render.secondary.InputNumberFormElementRenderer;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static io.micronaut.views.fields.tck.AsssertHtmlUtils.assertHtmlEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Property(name = "micronaut.views.form-element.render.views.input-number", value = "fieldset/inputnumber")
@MicronautTest(startApplication = false)
@SuppressWarnings({
    "java:S5960", // Assertions are fine, these are tests
    "java:S6813"  // As are field injections
})
class InputNumberFormElementRendererTest {

    @Inject
    FormElementRenderer<InputNumberFormElement> renderer;

    @Test
    void render() {
        assertFalse(renderer instanceof InputNumberFormElementRenderer);
        InputNumberFormElement el = InputNumberFormElement.builder()
            .label(Message.of("Number of tentacles (10-100):", null))
            .id("tentacles")
            .name("tentacles")
            .min(10)
            .max(100)
            .build();
        assertHtmlEquals("""
            <label for="tentacles" class="form-label">Number of tentacles (10-100):</label>\
            <input type="number" name="tentacles" value="" id="tentacles" min="10" max="100" class="form-control"/>""",
            renderer.render(el, Locale.ENGLISH)
        );
    }
}

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
import io.micronaut.views.fields.elements.Checkbox;
import io.micronaut.views.fields.elements.InputCheckboxFormElement;
import io.micronaut.views.fields.messages.Message;
import io.micronaut.views.fields.render.FormElementRenderer;
import io.micronaut.views.fields.render.secondary.InputCheckboxFormElementRenderer;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static io.micronaut.views.fields.tck.AsssertHtmlUtils.assertHtmlEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Property(name = "micronaut.views.form-element.render.views.input-checkbox", value = "fieldset/inputcheckbox")
@MicronautTest(startApplication = false)
@SuppressWarnings({
    "java:S5960", // Assertions are fine, these are tests
    "java:S6813"  // As are field injections
})
class InputCheckboxFormElementRendererTest {

    @Inject
    FormElementRenderer<InputCheckboxFormElement> renderer;

    @Test
    void render() {
        assertFalse(renderer instanceof InputCheckboxFormElementRenderer);
        InputCheckboxFormElement el = InputCheckboxFormElement.builder()
            .checkboxes(List.of(
                Checkbox.builder().id("scales").name("scales").label(Message.of("Scales", null)).checked(true).build(),
                Checkbox.builder().id("horns").name("horns").label(Message.of("Horns", null)).build()
            ))
            .build();
        assertHtmlEquals("""
            <div class="form-check"><input type="checkbox" name="scales" value="" id="scales" class="form-check-input" checked="checked"/><label for="scales" class="form-label">Scales</label></div>\
            <div class="form-check"><input type="checkbox" name="horns" value="" id="horns" class="form-check-input"/><label for="horns" class="form-label">Horns</label></div>""",
            renderer.render(el, Locale.ENGLISH).trim()
        );
    }
}

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
import io.micronaut.views.fields.elements.InputRadioFormElement;
import io.micronaut.views.fields.message.Message;
import io.micronaut.views.fields.elements.Radio;
import io.micronaut.views.fields.render.FormElementRenderer;
import io.micronaut.views.fields.render.secondary.InputRadioFormElementRenderer;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Property(name = "micronaut.views.form-element.render.views.input-radio", value = "fieldset/inputradios.html")
@MicronautTest(startApplication = false)
@SuppressWarnings({
    "java:S5960", // Assertions are fine, these are tests
    "java:S6813"  // As are field injections
})
class InputRadioFormElementRendererTest {

    @Inject
    FormElementRenderer<InputRadioFormElement> renderer;

    @Test
    void renderOption() {
        assertFalse(renderer instanceof InputRadioFormElementRenderer);
        InputRadioFormElement inputRadioFormElement = InputRadioFormElement.builder()
            .name("drone")
            .buttons(List.of(
                Radio.builder()
                    .id("huey")
                    .value("huey")
                    .checked(true)
                    .label(Message.of("Huey", null))
                    .build(),
                Radio.builder()
                    .id("dewey")
                    .value("dewey")
                    .label(Message.of("Dewey", null))
                    .build(),
                Radio.builder()
                    .id("louie")
                    .value("louie")
                    .label(Message.of("Louie", null))
                    .build()
            ))
            .build();
        assertEquals("""
                <div class="form-check"><input type="radio" name="drone" value="huey" id="huey" class="form-check-input" checked="checked"/><label for="huey" class="form-label">Huey</label></div>\
                <div class="form-check"><input type="radio" name="drone" value="dewey" id="dewey" class="form-check-input"/><label for="dewey" class="form-label">Dewey</label></div>\
                <div class="form-check"><input type="radio" name="drone" value="louie" id="louie" class="form-check-input"/><label for="louie" class="form-label">Louie</label></div>""",
            renderer.render(inputRadioFormElement, Locale.ENGLISH)
        );
    }
}

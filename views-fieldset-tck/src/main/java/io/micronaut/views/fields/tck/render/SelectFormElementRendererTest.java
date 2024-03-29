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
import io.micronaut.views.fields.messages.Message;
import io.micronaut.views.fields.elements.Option;
import io.micronaut.views.fields.elements.SelectFormElement;
import io.micronaut.views.fields.render.FormElementRenderer;
import io.micronaut.views.fields.render.secondary.SelectFormElementRenderer;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Property(name = "micronaut.views.form-element.render.views.select", value = "fieldset/select.html")
@MicronautTest(startApplication = false)
@SuppressWarnings({
    "java:S5960", // Assertions are fine, these are tests
    "java:S6813"  // As are field injections
})
class SelectFormElementRendererTest {

    @Inject
    FormElementRenderer<SelectFormElement> renderer;

    @Test
    void renderOption() {
        assertFalse(renderer instanceof SelectFormElementRenderer);
        SelectFormElement el = SelectFormElement.builder()
            .label(Message.of("Choose a pet:", null))
            .id("pet-select")
            .name("pets")
            .options(List.of(
                Option.builder()
                    .value("dog")
                    .label(Message.of("Dog", null))
                    .build(),
                Option.builder()
                    .value("cat")
                    .label(Message.of("Cat", null))
                    .build(),
                Option.builder()
                    .value("hamster")
                    .label(Message.of("Hamster", null))
                    .build(),
                Option.builder()
                    .value("parrot")
                    .label(Message.of("Parrot", null))
                    .build(),
                Option.builder()
                    .value("spider")
                    .label(Message.of("Spider", null))
                    .build(),
                Option.builder()
                    .value("goldfish")
                    .label(Message.of("Goldfish", null))
                    .build()
            ))
            .build();
        assertEquals("""
            <label for="pet-select" class="form-label">Choose a pet:</label><select name="pets" id="pet-select" class="form-select">\
            <option value="dog">Dog</option>\
            <option value="cat">Cat</option>\
            <option value="hamster">Hamster</option>\
            <option value="parrot">Parrot</option>\
            <option value="spider">Spider</option>\
            <option value="goldfish">Goldfish</option>\
            </select>""",
            renderer.render(el, Locale.ENGLISH)
        );
    }

}

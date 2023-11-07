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
import io.micronaut.views.fields.elements.Option;
import io.micronaut.views.fields.messages.SimpleMessage;
import io.micronaut.views.fields.render.FormElementRenderer;
import io.micronaut.views.fields.render.secondary.OptionFormElementRenderer;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@Property(name = "micronaut.views.form-element.render.views.option", value = "fieldset/option.html")
@MicronautTest(startApplication = false)
@SuppressWarnings({
    "java:S5960", // Assertions are fine, these are tests
    "java:S6813", // As are field injections
    "java:S5663"  // Single line Html in block strings are much easier to read
})
class OptionFormElementRenderTest {

    @Inject
    FormElementRenderer<Option> renderer;

    @Test
    void renderOption() {
        assertFalse(renderer instanceof OptionFormElementRenderer);
        Option option = Option.builder()
            .value("dog")
            .label(new SimpleMessage("Dog", null))
            .build();
        assertEquals("""
                <option value="dog">Dog</option>""",
            renderer.render(option, Locale.ENGLISH)
        );
        option = Option.builder()
            .value("dog")
            .label(new SimpleMessage("Dog", null))
            .selected(true)
            .disabled(true)
            .build();
        String result = renderer.render(option, Locale.ENGLISH);
        assertTrue("""
            <option value="dog" selected="selected" disabled="disabled">Dog</option>""".equals(result)
            || """
            <option value="dog" disabled="disabled" selected="selected">Dog</option>""".equals(result)
        );
    }
}

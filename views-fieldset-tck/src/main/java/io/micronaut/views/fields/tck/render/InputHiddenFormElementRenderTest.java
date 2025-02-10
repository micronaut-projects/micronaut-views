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
import io.micronaut.views.fields.elements.InputHiddenFormElement;
import io.micronaut.views.fields.render.FormElementRenderer;
import io.micronaut.views.fields.render.secondary.InputHiddenFormElementRenderer;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static io.micronaut.views.fields.tck.AsssertHtmlUtils.assertHtmlEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Property(name = "micronaut.views.form-element.render.views.input-hidden", value = "fieldset/inputhidden")
@MicronautTest(startApplication = false)
@SuppressWarnings({
    "java:S5960", // Assertions are fine, these are tests
    "java:S6813", // As are field injections
    "java:S5663"  // Single line Html in block strings are much easier to read
})
class InputHiddenFormElementRenderTest {

    @Inject
    FormElementRenderer<InputHiddenFormElement> renderer;

    @Test
    void renderOption() {
        assertFalse(renderer instanceof InputHiddenFormElementRenderer);
        InputHiddenFormElement el = InputHiddenFormElement.builder()
            .name("postId")
            .value("34657")
            .build();
        assertHtmlEquals("""
            <input type="hidden" name="postId" value="34657"/>""",
            renderer.render(el, Locale.ENGLISH)
        );
    }
}

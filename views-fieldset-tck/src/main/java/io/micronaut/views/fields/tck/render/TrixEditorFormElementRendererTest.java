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
import io.micronaut.views.fields.Message;
import io.micronaut.views.fields.TrixEditorFormElement;
import io.micronaut.views.fields.render.FormElementRenderer;
import io.micronaut.views.fields.render.secondary.TrixEditorFormElementRenderer;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Property(name = "micronaut.views.form-element-views.trix-editor", value = "fieldset/trixeditor.html")
@MicronautTest(startApplication = false)
@SuppressWarnings({
    "java:S5960", // Assertions are fine, these are tests
    "java:S6813"  // As are field injections
})
class TrixEditorFormElementRendererTest {

    @Inject
    FormElementRenderer<TrixEditorFormElement> renderer;

    @Test
    void render() {
        assertFalse(renderer instanceof TrixEditorFormElementRenderer);
        TrixEditorFormElement el = TrixEditorFormElement.builder()
            .name("content")
            .id("x")
            .value("Editor content goes here")
            .label(Message.of("Tell us your story:", null))
            .build();
        assertEquals("""
            <label for="x" class="form-label">Tell us your story:</label>\
            <input type="hidden" name="content" value="Editor content goes here" id="x"/><trix-editor input="x"></trix-editor>""",
            renderer.render(el, Locale.ENGLISH)
        );
    }
}

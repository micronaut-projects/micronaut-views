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
import io.micronaut.views.fields.TextareaFormElement;
import io.micronaut.views.fields.annotations.Textarea;
import io.micronaut.views.fields.render.FormElementRenderer;
import io.micronaut.views.fields.render.TextareaFormElementRenderer;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Property(name = "micronaut.views.form-element-views.textarea", value = "fieldset/textarea.html")
@MicronautTest(startApplication = false)
class TextareaFormElementRendererTest {
    @Inject
    FormElementRenderer<TextareaFormElement> renderer;

    @Test
    void render() {
        assertFalse(renderer instanceof TextareaFormElementRenderer);
        TextareaFormElement el = TextareaFormElement.builder()
            .name("story")
            .id("story")
            .rows(5)
            .cols(33)
            .value("It was a dark and stormy night...")
            .label(Message.of("Tell us your story:", null))
            .build();
        assertEquals("""
           <label for="story" class="form-label">Tell us your story:</label><textarea name="story" id="story" cols="33" rows="5" class="form-control">It was a dark and stormy night...</textarea>""",
            renderer.render(el, Locale.ENGLISH)
        );
    }
}

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
import io.micronaut.views.fields.InputSubmitFormElement;
import io.micronaut.views.fields.Message;
import io.micronaut.views.fields.render.FormElementRenderer;
import io.micronaut.views.fields.render.InputSubmitFormElementRenderer;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Property(name = "micronaut.views.form-element-views.input-submit", value = "fieldset/inputsubmit.html")
@MicronautTest(startApplication = false)
class InputSubmitFormElementRenderTest {
    @Inject
    FormElementRenderer<InputSubmitFormElement> renderer;

    @Test
    void renderOption() {
        assertFalse(renderer instanceof InputSubmitFormElementRenderer);
        Message value = Message.of("Send Request", null);
        InputSubmitFormElement el = InputSubmitFormElement.builder()
            .value(value)
            .build();
        assertEquals("""
            <input type="submit" value="Send Request" class="btn btn-primary"/>""",
            renderer.render(el, Locale.ENGLISH)
        );
    }
}

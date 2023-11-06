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
import io.micronaut.views.fields.InputPasswordFormElement;
import io.micronaut.views.fields.Message;
import io.micronaut.views.fields.render.FormElementRenderer;
import io.micronaut.views.fields.render.secondary.InputPasswordFormElementRenderer;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Property(name = "micronaut.views.form-element-views.input-password", value = "fieldset/inputpassword.html")
@MicronautTest(startApplication = false)
class InputPasswordFormElementRendererTest {
    @Inject
    FormElementRenderer<InputPasswordFormElement> renderer;

    @Test
    void render() {
        assertFalse(renderer instanceof InputPasswordFormElementRenderer);
        InputPasswordFormElement el = InputPasswordFormElement.builder()
            .name("password")
            .id("pass")
            .minLength(8)
            .required(true)
            .label(Message.of("Password (8 characters minimum):", null))
            .build();
        assertEquals("""
            <label for="pass" class="form-label">Password (8 characters minimum):</label><input type="password" name="password" value="" id="pass" minlength="8" class="form-control" required="required"/>""",
            renderer.render(el, Locale.ENGLISH)
        );
    }
}

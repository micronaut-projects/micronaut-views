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
package io.micronaut.views.fields.tck;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.elements.InputTextFormElement;
import io.micronaut.views.fields.messages.Message;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest(startApplication = false)
@SuppressWarnings({"java:S5960"}) // Assertions are fine, these are tests
class InputStringViewRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer) throws IOException {
        assertNotNull(viewsRenderer);

        InputTextFormElement el = InputTextFormElement.builder()
            .name("name")
            .id("name")
            .minLength(4)
            .maxLength(8)
            .size(10)
            .required(true)
            .label(Message.of("Name (4 to 8 characters):"))
            .build();
        assertEquals("""
                <label for="name" class="form-label">Name (4 to 8 characters):</label>\
                <input type="text" name="name" value="" id="name" minlength="4" maxlength="8" size="10" class="form-control" required="required"/>""",
            TestUtils.render("fieldset/inputstring.html", viewsRenderer, Map.of("type", "text", "el", el))
        );
    }
}

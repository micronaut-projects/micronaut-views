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
import io.micronaut.views.fields.Checkbox;
import io.micronaut.views.fields.InputCheckboxFormElement;
import io.micronaut.views.fields.Message;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest(startApplication = false)
class InputCheckboxViewRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer) throws IOException {
        assertNotNull(viewsRenderer);
        InputCheckboxFormElement el = InputCheckboxFormElement.builder()
                .label(Message.of("Attributes", "foobar"))
                .checkboxes(List.of(
                        Checkbox.builder().id("scales").name("scales").label(Message.of("Scales", null)).checked(true).build(),
                        Checkbox.builder().id("horns").name("horns").label(Message.of("Horns", null)).build()
                ))
                .build();
        assertEquals("""
            <label class="form-label">Attributes</label>\
            <div class="form-check"><input type="checkbox" name="scales" value="" id="scales" class="form-check-input" checked="checked"/><label for="scales" class="form-label">Scales</label></div>\
            <div class="form-check"><input type="checkbox" name="horns" value="" id="horns" class="form-check-input"/><label for="horns" class="form-label">Horns</label></div>""",
                TestUtils.render("fieldset/inputcheckbox.html", viewsRenderer, Map.of("el", el))
        );
    }
}

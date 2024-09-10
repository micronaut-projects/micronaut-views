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

import io.micronaut.core.annotation.Introspected;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.Form;
import io.micronaut.views.fields.FormGenerator;
import io.micronaut.views.fields.elements.Checkbox;
import io.micronaut.views.fields.elements.InputCheckboxFormElement;
import io.micronaut.views.fields.messages.Message;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
@SuppressWarnings({"java:S5960"}) // Assertions are fine, these are tests
class InputCheckboxViewRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer) throws IOException {
        assertNotNull(viewsRenderer);
        InputCheckboxFormElement el = InputCheckboxFormElement.builder()
            .label(Message.of("Attributes", "foobar"))
            .checkboxes(List.of(
                Checkbox.builder().id("scales").name("scales").label(Message.of("Scales")).checked(true).build(),
                Checkbox.builder().id("horns").name("horns").label(Message.of("Horns")).build(),
                Checkbox.builder().id("devils").name("devils").disabled(true).label(Message.of("Devils")).build()
            ))
            .build();
        assertEquals("""
                <label class="form-label">Attributes</label>\
                <div class="form-check">\
                <input type="checkbox" name="scales" value="" id="scales" class="form-check-input" checked="checked"/>\
                <label for="scales" class="form-label">Scales</label>\
                </div>\
                <div class="form-check">\
                <input type="checkbox" name="horns" value="" id="horns" class="form-check-input"/>\
                <label for="horns" class="form-label">Horns</label>\
                </div>\
                <div class="form-check">\
                <input type="checkbox" name="devils" value="" id="devils" class="form-check-input" disabled="disabled"/>\
                <label for="devils" class="form-label">Devils</label>\
                </div>""",
            TestUtils.render("fieldset/inputcheckbox.html", viewsRenderer, Map.of("el", el)).trim()
        );
    }

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer, FormGenerator formGenerator) throws IOException {
        assertNotNull(viewsRenderer);
        Form form = formGenerator.generate("/login", SigninForm.class);
        String html = TestUtils.render("fieldset/form.html", viewsRenderer, Map.of("form", form)).trim();
        assertEquals(1, TestUtils.countOccurrences(html, "Remember Me"));
    }
}

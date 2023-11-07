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
import io.micronaut.views.fields.elements.InputRadioFormElement;
import io.micronaut.views.fields.message.Message;
import io.micronaut.views.fields.elements.Radio;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(startApplication = false)
@SuppressWarnings({"java:S5960"}) // Assertions are fine, these are tests
class InputRadioViewRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer) throws IOException {
        assertNotNull(viewsRenderer);

        Radio huey = Radio.builder()
            .id("huey")
            .value("huey")
            .checked(true)
            .label(Message.of("Huey"))
            .build();
        InputRadioFormElement el = InputRadioFormElement.builder()
            .name("drone")
            .buttons(List.of(
                huey,
                Radio.builder()
                    .id("dewey")
                    .value("dewey")
                    .label(Message.of("Dewey"))
                    .build(),
                Radio.builder()
                    .id("louie")
                    .value("louie")
                    .label(Message.of("Louie"))
                    .build()
            ))
            .build();
        String html = TestUtils.render("fieldset/inputradio.html", viewsRenderer, Map.of("el", el, "radio", huey));

        assertTrue(
            "<input type=\"radio\" name=\"drone\" value=\"huey\" id=\"huey\" class=\"form-check-input\" checked/>".equals(html)
                || "<input type=\"radio\" name=\"drone\" value=\"huey\" id=\"huey\" class=\"form-check-input\" checked=\"checked\"/>".equals(html)
        );

        html = TestUtils.render("fieldset/inputradios.html", viewsRenderer, Map.of("el", el));
        assertEquals("""
                <div class="form-check"><input type="radio" name="drone" value="huey" id="huey" class="form-check-input" checked="checked"/><label for="huey" class="form-label">Huey</label></div>\
                <div class="form-check"><input type="radio" name="drone" value="dewey" id="dewey" class="form-check-input"/><label for="dewey" class="form-label">Dewey</label></div>\
                <div class="form-check"><input type="radio" name="drone" value="louie" id="louie" class="form-check-input"/><label for="louie" class="form-label">Louie</label></div>""",
            html
        );
    }
}

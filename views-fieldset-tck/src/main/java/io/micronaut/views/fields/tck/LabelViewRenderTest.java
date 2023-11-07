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
import io.micronaut.views.fields.messages.Message;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest(startApplication = false)
@SuppressWarnings({"java:S5960"}) // Assertions are fine, these are tests
class LabelViewRenderTest {

    private static final String FOO_BAR = "Foo Bar";

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer) throws IOException {
        assertNotNull(viewsRenderer);
        Message message = Message.of(FOO_BAR, "foo.bar");

        String id = "identifier";
        String expected = """
            <label for="identifier" class="form-label">%s</label>""".formatted(FOO_BAR);

        assertEquals(expected, TestUtils.render("fieldset/label.html", viewsRenderer, Map.of("id", id, "el", message)));

        message = Message.of(FOO_BAR);
        assertEquals(expected, TestUtils.render("fieldset/label.html", viewsRenderer, Map.of("id", id, "el", message)));

        expected = """
            <label class="form-label">%s</label>""".formatted(FOO_BAR);

        message = Message.of(FOO_BAR);
        assertEquals(expected, TestUtils.render("fieldset/label.html", viewsRenderer, Map.of("el", message)));

        message = Message.of(FOO_BAR, "foo.bar");
        assertEquals(expected, TestUtils.render("fieldset/label.html", viewsRenderer, Map.of("el", message)));
    }
}

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

import io.micronaut.core.io.Writable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.Message;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest(startApplication = false)
class LabelViewRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer) throws IOException {
        assertNotNull(viewsRenderer);
        Message message = Message.of("Foo Bar", "foo.bar");
        String id = "identifier";
        String expected = "<label for=\"identifier\" class=\"form-label\">Foo Bar</label>";
        assertEquals(expected, render(viewsRenderer, id, message));

        message = Message.of("Foo Bar", null);
        assertEquals(expected, render(viewsRenderer, id, message));

        expected = "<label class=\"form-label\">Foo Bar</label>";
        message = Message.of("Foo Bar", null);
        assertEquals(expected, render(viewsRenderer, null, message));

        message = Message.of("Foo Bar", "foo.bar");
        assertEquals(expected, render(viewsRenderer, null, message));
    }

    private static String render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer, String id, Message el) throws IOException {
        return output(viewsRenderer.render("fieldset/label.html", id != null ? Map.of("id", id, "el", el) : Collections.singletonMap("el", el), null));
    }

    private static String output(Writable writeable) throws IOException {
        StringWriter sw = new StringWriter();
        writeable.writeTo(sw);
        return sw.toString();
    }
}

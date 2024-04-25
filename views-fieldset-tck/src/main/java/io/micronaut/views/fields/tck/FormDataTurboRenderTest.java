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
import io.micronaut.views.fields.Fieldset;
import io.micronaut.views.fields.Form;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SuppressWarnings({"java:S5960"}) // Assertions are fine, these are tests
@MicronautTest(startApplication = false)
class FormDataTurboRenderTest {

    @Test
    void renderDataTurboTrueByDefault(ViewsRenderer<Map<String, Object>, ?> viewsRenderer) throws IOException {
        assertNotNull(viewsRenderer);
        Form form = new Form("/foo/bar", "post", new Fieldset(Collections.emptyList(), Collections.emptyList()), "application/x-www-form-urlencoded");
        assertEquals("""
                <form action="/foo/bar" method="post" enctype="application/x-www-form-urlencoded">\
                </form>""",
                TestUtils.render("fieldset/form.html", viewsRenderer, Map.of("form", form)));
    }

    @Test
    void renderDataTurboTrue(ViewsRenderer<Map<String, Object>, ?> viewsRenderer) throws IOException {
        assertNotNull(viewsRenderer);
        Form form = new Form("/foo/bar", "post", new Fieldset(Collections.emptyList(), Collections.emptyList()), "application/x-www-form-urlencoded", true);
        assertEquals("""
                <form action="/foo/bar" method="post" enctype="application/x-www-form-urlencoded" data-turbo="true">\
                </form>""",
                TestUtils.render("fieldset/form.html", viewsRenderer, Map.of("form", form)));
    }

    @Test
    void renderDataTurboFalse(ViewsRenderer<Map<String, Object>, ?> viewsRenderer) throws IOException {
        assertNotNull(viewsRenderer);
        Form form = new Form("/foo/bar", "post", new Fieldset(Collections.emptyList(), Collections.emptyList()), "application/x-www-form-urlencoded", false);
        assertEquals("""
                <form action="/foo/bar" method="post" enctype="application/x-www-form-urlencoded" data-turbo="false">\
                </form>""",
                TestUtils.render("fieldset/form.html", viewsRenderer, Map.of("form", form)));
    }
}

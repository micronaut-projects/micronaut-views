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
import io.micronaut.core.annotation.NonNull;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.Fieldset;
import io.micronaut.views.fields.FieldsetGenerator;
import io.micronaut.views.fields.Form;
import io.micronaut.views.fields.elements.InputHiddenFormElement;
import io.micronaut.views.fields.annotations.InputHidden;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static io.micronaut.views.fields.tck.TestUtils.assertEqualsIgnoreSpace;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest(startApplication = false)
@SuppressWarnings({"java:S5960"}) // Assertions are fine, these are tests
class InputHiddenViewRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer,
                FieldsetGenerator fieldsetGenerator,
                FormValidator formValidator) throws IOException {
        assertNotNull(viewsRenderer);
        InputHiddenFormElement el = InputHiddenFormElement.builder()
            .name("postId")
            .value("34657")
            .build();

        Form form = new Form("/post/save", "post", fieldsetGenerator.generate(new Post(34657L)));
        assertEqualsIgnoreSpace("""
                <form action="/post/save" method="post">\
                <input type="hidden" name="postId" value="34657"/>\
                </form>""",
            TestUtils.render("fieldset/form", viewsRenderer, Map.of("form", form))
        );

        @SuppressWarnings("java:S2637") // We're passing null on purpose
        Post invalid = new Post(null);
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> formValidator.validate(invalid));
        form = new Form("/post/save", "post", fieldsetGenerator.generate(invalid, ex));
        assertEqualsIgnoreSpace("""
                <form action="/post/save" method="post">\
                <input type="hidden" name="postId" value=""/>\
                </form>""",
            TestUtils.render("fieldset/form", viewsRenderer, Map.of("form", form))
        );

        assertEqualsIgnoreSpace("""
                <input type="hidden" name="postId" value="34657"/>""",
            TestUtils.render("fieldset/inputhidden", viewsRenderer, Map.of("el", el))
        );

        Fieldset fieldset = fieldsetGenerator.generate(new Post(34657L));

        assertEqualsIgnoreSpace("""
                <input type="hidden" name="postId" value="34657"/>""",
            TestUtils.render("fieldset/fieldset", viewsRenderer, Map.of("el", fieldset))
        );
    }


    @Introspected
    record Post(@InputHidden @NonNull @NotNull Long postId) {
    }
}

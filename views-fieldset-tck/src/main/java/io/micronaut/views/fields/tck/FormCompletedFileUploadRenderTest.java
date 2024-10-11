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

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.Form;
import io.micronaut.views.fields.FormElement;
import io.micronaut.views.fields.FormGenerator;
import io.micronaut.views.fields.annotations.InputHidden;
import io.micronaut.views.fields.elements.InputSubmitFormElement;
import io.micronaut.views.fields.messages.Message;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;

import static io.micronaut.views.fields.tck.AsssertHtmlUtils.assertHtmlEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings({"java:S5960"}) // Assertions are fine, these are tests
@Property(name = "spec.name", value = "FormCompletedFileUploadRenderTest")
@MicronautTest(startApplication = false)
class FormCompletedFileUploadRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer,
                FormGenerator formGenerator,
                EventImageSaveValidator validator) throws IOException {
        String viewName = "fieldset/form.html";
        BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer = (propertyName, builder) -> {
            if (propertyName.equals("file")) {
                builder.with("accept", "image/png, image/jpeg");
            }
            if (propertyName.equals("alt")) {
                builder.with("label", Message.of("Alternative description of the image"));
            }
        };
        String expectedClass = """
                        <form action="/foo/bar" method="post" enctype="multipart/form-data">\
                        <input type="hidden" name="id" value=""/>\
                        <div class="mb-3"><label for="alt" class="form-label">Alternative description of the image</label><input type="text" name="alt" value="" id="alt" class="form-control" required="required"/></div>\
                        <label for="file" class="form-label">File</label><input type="file" name="file" id="file" accept="image/png, image/jpeg" class="form-control" required="required"/>\
                        <input type="submit" value="Submit" class="btn btn-primary"/>\
                        </form>""";
        Form form = formGenerator.generate("/foo/bar", "post", EventImageSave.class, builderConsumer);
        assertHtmlEquals(expectedClass, TestUtils.render(viewName, viewsRenderer, Map.of("form", form)));

        form = formGenerator.generate("/foo/bar", "post", EventImageSave.class, FormGenerator.SUBMIT, builderConsumer);
        assertHtmlEquals(expectedClass, TestUtils.render(viewName, viewsRenderer, Map.of("form", form)));

        form = formGenerator.generate("/foo/bar", "post", EventImageSave.class, new InputSubmitFormElement(FormGenerator.SUBMIT), builderConsumer);
        assertHtmlEquals(expectedClass, TestUtils.render(viewName, viewsRenderer, Map.of("form", form)));

        form = formGenerator.generate("/foo/bar", EventImageSave.class, builderConsumer);
        assertHtmlEquals(expectedClass, TestUtils.render(viewName, viewsRenderer, Map.of("form", form)));

        form = formGenerator.generate("/foo/bar", EventImageSave.class, FormGenerator.SUBMIT, builderConsumer);
        assertHtmlEquals(expectedClass, TestUtils.render(viewName, viewsRenderer, Map.of("form", form)));

        form = formGenerator.generate("/foo/bar", EventImageSave.class, new InputSubmitFormElement(FormGenerator.SUBMIT), builderConsumer);
        assertHtmlEquals(expectedClass, TestUtils.render(viewName, viewsRenderer, Map.of("form", form)));

        EventImageSave invalid = new EventImageSave("xxx", "", null);
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> validator.validate(invalid));
        String expectedInvalid = """
                        <form action="/foo/bar" method="post" enctype="multipart/form-data">\
                        <input type="hidden" name="id" value="xxx"/>\
                        <div class="mb-3"><label for="alt" class="form-label">Alternative description of the image</label>\
                        <input type="text" name="alt" value="" id="alt" class="form-control is-invalid" aria-describedby="altValidationServerFeedback" required="required"/>\
                        <div id="altValidationServerFeedback" class="invalid-feedback">must not be blank</div>\
                        </div>\
                        <label for="file" class="form-label">File</label>\
                        <input type="file" name="file" id="file" accept="image/png, image/jpeg" class="form-control is-invalid" aria-describedby="fileValidationServerFeedback" required="required"/>\
                        <div id="fileValidationServerFeedback" class="invalid-feedback">must not be null</div>\
                        <input type="submit" value="Submit" class="btn btn-primary"/>\
                        </form>""";
        form = formGenerator.generate("/foo/bar", "post", invalid, ex, builderConsumer);
        assertHtmlEquals(expectedInvalid, TestUtils.render(viewName, viewsRenderer, Map.of("form", form)));

        form = formGenerator.generate("/foo/bar", "post", invalid, ex, FormGenerator.SUBMIT, builderConsumer);
        assertHtmlEquals(expectedInvalid, TestUtils.render(viewName, viewsRenderer, Map.of("form", form)));

        form = formGenerator.generate("/foo/bar", "post", invalid, ex, new InputSubmitFormElement(FormGenerator.SUBMIT), builderConsumer);
        assertHtmlEquals(expectedInvalid, TestUtils.render(viewName, viewsRenderer, Map.of("form", form)));

        form = formGenerator.generate("/foo/bar",  invalid, ex, new InputSubmitFormElement(FormGenerator.SUBMIT), builderConsumer);
        assertHtmlEquals(expectedInvalid, TestUtils.render(viewName, viewsRenderer, Map.of("form", form)));

        form = formGenerator.generate("/foo/bar",  invalid, ex, builderConsumer);
        assertHtmlEquals(expectedInvalid, TestUtils.render(viewName, viewsRenderer, Map.of("form", form)));

        form = formGenerator.generate("/foo/bar",  invalid, ex, FormGenerator.SUBMIT, builderConsumer);
        assertHtmlEquals(expectedInvalid, TestUtils.render(viewName, viewsRenderer, Map.of("form", form)));

        form = formGenerator.generate("/foo/bar",  invalid, ex, new InputSubmitFormElement(FormGenerator.SUBMIT), builderConsumer);
        assertHtmlEquals(expectedInvalid, TestUtils.render(viewName, viewsRenderer, Map.of("form", form)));

        EventImageSave valid = new EventImageSave("xxx", "Micronaut Logo", null);
        String expectedValid = """
                        <form action="/foo/bar" method="post" enctype="multipart/form-data">\
                        <input type="hidden" name="id" value="xxx"/>\
                        <div class="mb-3"><label for="alt" class="form-label">Alternative description of the image</label><input type="text" name="alt" value="Micronaut Logo" id="alt" class="form-control" required="required"/></div>\
                        <label for="file" class="form-label">File</label><input type="file" name="file" id="file" accept="image/png, image/jpeg" class="form-control" required="required"/>\
                        <input type="submit" value="Submit" class="btn btn-primary"/>\
                        </form>""";
        form = formGenerator.generate("/foo/bar", "post", valid, builderConsumer);
        assertHtmlEquals(expectedValid,
                TestUtils.render(viewName, viewsRenderer, Map.of("form", form)));

        form = formGenerator.generate("/foo/bar", valid, builderConsumer);
        assertHtmlEquals(expectedValid,
            TestUtils.render(viewName, viewsRenderer, Map.of("form", form)));

        form = formGenerator.generate("/foo/bar", valid, FormGenerator.SUBMIT, builderConsumer);
        assertHtmlEquals(expectedValid,
            TestUtils.render(viewName, viewsRenderer, Map.of("form", form)));

        form = formGenerator.generate("/foo/bar", valid, new InputSubmitFormElement(FormGenerator.SUBMIT), builderConsumer);
        assertHtmlEquals(expectedValid,
            TestUtils.render(viewName, viewsRenderer, Map.of("form", form)));
    }

    @Introspected
    record EventImageSave(@NotBlank @InputHidden String id,
                          @NotBlank String alt,
                          @NotNull CompletedFileUpload file) {
    }

    @Requires(property = "spec.name", value = "FormCompletedFileUploadRenderTest")
    @Singleton
    static class EventImageSaveValidator {
        void validate(@Valid EventImageSave eventImageSave) {
        }
    }

}

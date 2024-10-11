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
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.Fieldset;
import io.micronaut.views.fields.FieldsetGenerator;
import io.micronaut.views.fields.annotations.InputEmail;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static io.micronaut.views.fields.tck.AsssertHtmlUtils.assertHtmlEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Property(name = "spec.name", value = "InputEmailViewRenderTest")
@MicronautTest(startApplication = false)
@SuppressWarnings({"java:S5960"}) // Assertions are fine, these are tests
class InputEmailViewRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer,
                FieldsetGenerator fieldsetGenerator,
                FormValidator formValidator) throws IOException {
        assertNotNull(viewsRenderer);

        Fieldset fieldset = fieldsetGenerator.generate(new Login("advocate@micronaut.io"));
        assertHtmlEquals("""
                <div class="mb-3">\
                <label for="email" class="form-label">Email</label>\
                <input type="email" name="email" value="advocate@micronaut.io" id="email" class="form-control" required="required"/>\
                </div>""",
            TestUtils.render("fieldset/fieldset", viewsRenderer, Map.of("el", fieldset))
        );


        @SuppressWarnings("java:S2637") // We're passing null on purpose
        Login invalid = new Login(null);
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> formValidator.validate(invalid));
        fieldset = fieldsetGenerator.generate(invalid, ex);
        assertHtmlEquals("""
                <div class="mb-3">\
                <label for="email" class="form-label">Email</label>\
                <input type="email" name="email" value="" id="email" class="form-control is-invalid" aria-describedby="emailValidationServerFeedback" required="required"/>\
                <div id="emailValidationServerFeedback" class="invalid-feedback">must not be blank</div>\
                </div>""",
            TestUtils.render("fieldset/fieldset", viewsRenderer, Map.of("el", fieldset))
        );
    }

    @Introspected
    record Login(@InputEmail @NonNull @Email @NotBlank String email) {
    }
}

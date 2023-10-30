package io.micronaut.views.fields.tck;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.*;
import io.micronaut.views.fields.annotations.InputEmail;
import io.micronaut.views.fields.annotations.InputHidden;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Property(name = "spec.name", value = "InputEmailViewRenderTest")
@MicronautTest(startApplication = false)
class InputEmailViewRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer,
                FieldsetGenerator fieldsetGenerator,
                FormValidator formValidator) throws IOException {
        assertNotNull(viewsRenderer);

        Fieldset fieldset = fieldsetGenerator.generate(new Login("advocate@micronaut.io"));
        assertEquals("""
        <div class="mb-3"><label for="email" class="form-label">Email</label><input type="email" name="email" value="advocate@micronaut.io" id="email" class="form-control" required="required"/></div>""", TestUtils.render("fieldset/fieldset.html", viewsRenderer, Map.of("el", fieldset)));


        Login invalid = new Login(null);
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> formValidator.validate(invalid));
        fieldset = fieldsetGenerator.generate(invalid, ex);
        assertEquals("""
        <div class="mb-3"><label for="email" class="form-label">Email</label><input type="email" name="email" value="" id="email" class="form-control is-invalid" aria-describedby="emailValidationServerFeedback" required="required"/><div id="emailValidationServerFeedback" class="invalid-feedback">must not be blank</div></div>""", TestUtils.render("fieldset/fieldset.html", viewsRenderer, Map.of("el", fieldset)));
    }

    @Introspected
    record Login(@InputEmail @NonNull @Email @NotBlank String email) {
    }
}

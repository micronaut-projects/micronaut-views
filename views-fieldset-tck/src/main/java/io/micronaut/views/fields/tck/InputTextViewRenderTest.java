package io.micronaut.views.fields.tck;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.Fieldset;
import io.micronaut.views.fields.FieldsetGenerator;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Property(name = "spec.name", value = "InputTextViewRenderTest")
@MicronautTest(startApplication = false)
class InputTextViewRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer,
                FieldsetGenerator fieldsetGenerator,
                FormValidator formValidator) throws IOException {
        assertNotNull(viewsRenderer);

        Fieldset fieldset = fieldsetGenerator.generate(new Contact("Sergio"));
        assertEquals("""
        <div class="mb-3"><label for="name" class="form-label">Name</label><input type="text" name="name" value="Sergio" id="name" class="form-control" required="required"/></div>""", TestUtils.render("fieldset/fieldset.html", viewsRenderer, Map.of("el", fieldset)));


        Contact invalid = new Contact(null);
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> formValidator.validate(invalid));
        fieldset = fieldsetGenerator.generate(invalid, ex);
        assertEquals("""
        <div class="mb-3"><label for="name" class="form-label">Name</label><input type="text" name="name" value="" id="name" class="form-control is-invalid" aria-describedby="nameValidationServerFeedback" required="required"/><div id="nameValidationServerFeedback" class="invalid-feedback">must not be blank</div></div>""", TestUtils.render("fieldset/fieldset.html", viewsRenderer, Map.of("el", fieldset)));
    }

    @Introspected
    record Contact(@NonNull @NotBlank String name) {
    }
}

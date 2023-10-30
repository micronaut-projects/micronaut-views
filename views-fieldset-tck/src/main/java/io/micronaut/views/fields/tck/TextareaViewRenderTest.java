package io.micronaut.views.fields.tck;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.Fieldset;
import io.micronaut.views.fields.FieldsetGenerator;
import io.micronaut.views.fields.annotations.InputEmail;
import io.micronaut.views.fields.annotations.Textarea;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class TextareaViewRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer,
                FieldsetGenerator fieldsetGenerator,
                FormValidator formValidator) throws IOException {
        assertNotNull(viewsRenderer);

        Fieldset fieldset = fieldsetGenerator.generate(new Post("bla bla bla"));
        assertEquals("""
        <div class="mb-3"><label for="description" class="form-label">Description</label><textarea name="description" id="description" class="form-control">bla bla bla</textarea></div>""", TestUtils.render("fieldset/fieldset.html", viewsRenderer, Map.of("el", fieldset)));


        Post invalid = new Post(null);
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> formValidator.validate(invalid));
        fieldset = fieldsetGenerator.generate(invalid, ex);
        assertEquals("""
                <div class="mb-3"><label for="description" class="form-label">Description</label><textarea name="description" id="description" class="form-control is-invalid" aria-describedby="descriptionValidationServerFeedback"></textarea><div id="descriptionValidationServerFeedback" class="invalid-feedback">must not be blank</div></div>""", TestUtils.render("fieldset/fieldset.html", viewsRenderer, Map.of("el", fieldset)));
    }

    @Introspected
    record Post(@Textarea @NotBlank String description) {
    }
}

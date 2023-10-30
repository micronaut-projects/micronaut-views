package io.micronaut.views.fields.tck;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.Fieldset;
import io.micronaut.views.fields.FieldsetGenerator;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Property(name = "spec.name", value = "InputNumberViewRenderTest")
@MicronautTest(startApplication = false)
class InputNumberViewRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer,
                FieldsetGenerator fieldsetGenerator,
                FormValidator formValidator) throws IOException {
        assertNotNull(viewsRenderer);

        Fieldset fieldset = fieldsetGenerator.generate(new Book(125));
        assertEquals("""
        <div class="mb-3"><label for="pages" class="form-label">Pages</label><input type="number" name="pages" value="125" id="pages" class="form-control" required="required"/></div>""", TestUtils.render("fieldset/fieldset.html", viewsRenderer, Map.of("el", fieldset)));

        Book invalid = new Book(null);
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> formValidator.validate(invalid));
        fieldset = fieldsetGenerator.generate(invalid, ex);
        assertEquals("""
        <div class="mb-3"><label for="pages" class="form-label">Pages</label><input type="number" name="pages" value="" id="pages" class="form-control is-invalid" aria-describedby="pagesValidationServerFeedback" required="required"/><div id="pagesValidationServerFeedback" class="invalid-feedback">must not be null</div></div>""", TestUtils.render("fieldset/fieldset.html", viewsRenderer, Map.of("el", fieldset)));
    }

    @Introspected
    record Book(@NotNull Integer pages) {
    }
}

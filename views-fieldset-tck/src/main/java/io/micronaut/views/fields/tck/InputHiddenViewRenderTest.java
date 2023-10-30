package io.micronaut.views.fields.tck;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.Fieldset;
import io.micronaut.views.fields.FieldsetGenerator;
import io.micronaut.views.fields.Form;
import io.micronaut.views.fields.InputHiddenFormElement;
import io.micronaut.views.fields.annotations.InputHidden;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Property(name = "spec.name", value = "InputHiddenViewRenderTest")
@MicronautTest(startApplication = false)
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
        assertEquals("""
        <input type="hidden" name="postId" value="34657"/>""", TestUtils.render("fieldset/inputhidden.html", viewsRenderer, Map.of("el", el)));

        Fieldset fieldset = fieldsetGenerator.generate(new Post(34657L));

        assertEquals("""
        <input type="hidden" name="postId" value="34657"/>""", TestUtils.render("fieldset/fieldset.html", viewsRenderer, Map.of("el", fieldset)));

        Form form = new Form("/post/save", "post", fieldsetGenerator.generate(new Post(34657L)));
        assertEquals("""
        <form action="/post/save" method="post"><input type="hidden" name="postId" value="34657"/></form>""", TestUtils.render("fieldset/form.html", viewsRenderer, Map.of("form", form)));

        Post invalid = new Post(null);
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> formValidator.validate(invalid));
        form = new Form("/post/save", "post", fieldsetGenerator.generate(invalid, ex));
        assertEquals("""
        <form action="/post/save" method="post"><input type="hidden" name="postId" value=""/></form>""", TestUtils.render("fieldset/form.html", viewsRenderer, Map.of("form", form)));
    }


    @Introspected
    record Post(@InputHidden @NonNull @NotNull Long postId) {
    }
}

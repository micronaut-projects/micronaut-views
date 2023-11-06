package io.micronaut.views.fields.thymeleaf.render;

import io.micronaut.context.annotation.Property;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.InputHiddenFormElement;
import io.micronaut.views.fields.render.FormElementRenderer;
import io.micronaut.views.fields.render.InputHiddenFormElementRenderer;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Property(name = "micronaut.views.form-element-views.input-hidden", value = "fieldset/inputhidden.html")
@MicronautTest(startApplication = false)
class InputHiddenFormElementRenderTest {
    @Inject
    FormElementRenderer<InputHiddenFormElement> renderer;
    @Test
    void renderOption() {
        assertFalse(renderer instanceof InputHiddenFormElementRenderer);
        InputHiddenFormElement el = InputHiddenFormElement.builder()
            .name("postId")
            .value("34657")
            .build();
        assertEquals("""
            <input type="hidden" name="postId" value="34657"/>""",
            renderer.render(el, Locale.ENGLISH)
        );
    }
}

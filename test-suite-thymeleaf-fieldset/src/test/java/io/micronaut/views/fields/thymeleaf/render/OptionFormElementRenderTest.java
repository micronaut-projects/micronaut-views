package io.micronaut.views.fields.thymeleaf.render;

import io.micronaut.context.annotation.Property;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.Option;
import io.micronaut.views.fields.SimpleMessage;
import io.micronaut.views.fields.render.FormElementRenderer;
import io.micronaut.views.fields.render.InputHiddenFormElementRenderer;
import io.micronaut.views.fields.render.OptionFormElementRenderer;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Property(name = "micronaut.views.form-element-views.option", value = "fieldset/option.html")
@MicronautTest(startApplication = false)
class OptionFormElementRenderTest {
    @Inject
    FormElementRenderer<Option> renderer;

    @Test
    void renderOption() {
        assertFalse(renderer instanceof OptionFormElementRenderer);
        Option option = Option.builder()
            .value("dog")
            .label(new SimpleMessage("Dog", null))
            .build();
        assertEquals("""
            <option value="dog">Dog</option>""",
            renderer.render(option, Locale.ENGLISH)
        );
        option = Option.builder()
            .value("dog")
            .label(new SimpleMessage("Dog", null))
            .selected(true)
            .disabled(true)
            .build();
        assertEquals("""
            <option value="dog" disabled="disabled" selected="selected">Dog</option>""",
            renderer.render(option, Locale.ENGLISH)
        );
    }
}

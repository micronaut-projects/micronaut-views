package io.micronaut.views.fields.render;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.elements.Option;
import io.micronaut.views.fields.message.SimpleMessage;
import io.micronaut.views.fields.render.secondary.OptionFormElementRenderer;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class OptionFormElementRenderTest {

    @Test
    void renderOption(OptionFormElementRenderer optionFormElementRender) {
        Option option = Option.builder()
            .value("dog")
            .label(new SimpleMessage("Dog", null))
            .build();
        assertEquals("""
            <option value="dog">Dog</option>""",
            optionFormElementRender.render(option, Locale.ENGLISH)
        );
        option = Option.builder()
            .value("dog")
            .label(new SimpleMessage("Dog", null))
            .selected(true)
            .disabled(true)
            .build();
        assertEquals("""
            <option value="dog" selected disabled>Dog</option>""",
            optionFormElementRender.render(option, Locale.ENGLISH)
        );
    }
}

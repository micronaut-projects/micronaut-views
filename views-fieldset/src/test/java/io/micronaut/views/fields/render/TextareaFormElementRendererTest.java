package io.micronaut.views.fields.render;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.messages.Message;
import io.micronaut.views.fields.elements.TextareaFormElement;
import io.micronaut.views.fields.render.secondary.TextareaFormElementRenderer;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest(startApplication = false)
class TextareaFormElementRendererTest {

    @Test
    void render(TextareaFormElementRenderer renderer) {
        TextareaFormElement el = TextareaFormElement.builder()
            .name("story")
            .id("story")
            .rows(5)
            .cols(33)
            .value("It was a dark and stormy night...")
            .label(Message.of("Tell us your story:"))
            .build();
        assertEquals("""
           <label for="story">Tell us your story:</label><textarea name="story" id="story" cols="33" rows="5">It was a dark and stormy night...</textarea>""",
            renderer.render(el, Locale.ENGLISH)
        );
    }
}

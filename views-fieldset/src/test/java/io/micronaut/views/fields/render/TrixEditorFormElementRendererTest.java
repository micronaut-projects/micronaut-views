package io.micronaut.views.fields.render;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.message.Message;
import io.micronaut.views.fields.elements.TrixEditorFormElement;
import io.micronaut.views.fields.render.secondary.TrixEditorFormElementRenderer;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest(startApplication = false)
class TrixEditorFormElementRendererTest {

    @Test
    void render(TrixEditorFormElementRenderer renderer) {
        TrixEditorFormElement el = TrixEditorFormElement.builder()
            .name("content")
            .id("x")
            .value("Editor content goes here")
            .label(Message.of("Tell us your story:"))
            .build();
        assertEquals("""
            <label for="x">Tell us your story:</label><input type="hidden" name="content" id="x" value="Editor content goes here"/><trix-editor input="x"></trix-editor>""",
            renderer.render(el, Locale.ENGLISH)
        );
    }
}

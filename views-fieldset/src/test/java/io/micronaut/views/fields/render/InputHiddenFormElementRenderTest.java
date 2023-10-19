package io.micronaut.views.fields.render;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.InputHiddenFormElement;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class InputHiddenFormElementRenderTest {
    @Test
    void renderOption(InputHiddenFormElementRenderer renderer) {
        InputHiddenFormElement el = InputHiddenFormElement.builder()
            .name("postId")
            .value("34657")
            .build();
        assertEquals("<input type=\"hidden\" name=\"postId\" value=\"34657\"/>", renderer.render(el, Locale.ENGLISH));
    }
}

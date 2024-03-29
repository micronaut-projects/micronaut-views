package io.micronaut.views.fields.render;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.elements.Option;
import io.micronaut.views.fields.elements.SelectFormElement;
import io.micronaut.views.fields.messages.Message;
import io.micronaut.views.fields.render.secondary.SelectFormElementRenderer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class SelectFormElementRendererTest {

    @Test
    void renderOption(SelectFormElementRenderer renderer) {
        SelectFormElement el = SelectFormElement.builder()
            .label(Message.of("Choose a pet:"))
            .id("pet-select")
            .name("pets")
            .options(List.of(
                Option.builder()
                    .value("dog")
                    .label(Message.of("Dog"))
                    .build(),
                Option.builder()
                    .value("cat")
                    .label(Message.of("Cat"))
                    .build(),
                Option.builder()
                    .value("hamster")
                    .label(Message.of("Hamster"))
                    .build(),
                Option.builder()
                    .value("parrot")
                    .label(Message.of("Parrot"))
                    .build(),
                Option.builder()
                    .value("spider")
                    .label(Message.of("Spider"))
                    .build(),
                Option.builder()
                    .value("goldfish")
                    .label(Message.of("Goldfish"))
                    .build()
            ))
            .build();
        assertEquals("""
            <label for="pet-select">Choose a pet:</label><select name="pets" id="pet-select">\
            <option value="dog">Dog</option>\
            <option value="cat">Cat</option>\
            <option value="hamster">Hamster</option>\
            <option value="parrot">Parrot</option>\
            <option value="spider">Spider</option>\
            <option value="goldfish">Goldfish</option>\
            </select>""",
            renderer.render(el, Locale.ENGLISH)
        );
    }

}

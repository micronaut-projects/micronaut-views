package io.micronaut.views.fields.formsexamples;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static io.micronaut.views.fields.formsexamples.FormElementFixture.assertFormElement;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class EnumDefaultsToSelectTest {
    @Introspected
    record EventCreateForm(
        @NotBlank String name,
        @NotNull Genre genre) {
    }

    @Test
    void renderEnumByDefaultAsSelect(FieldsetGenerator fieldsetGenerator) {
        Fieldset fieldset = fieldsetGenerator.generate(EventCreateForm.class);
        assertNotNull(fieldset);
        assertEquals(2, fieldset.getFields().size());
        assertTrue(fieldset.getFields().stream().anyMatch(formElement -> formElement instanceof InputTextFormElement));
        assertTrue(fieldset.getFields().stream().anyMatch(formElement -> formElement instanceof SelectFormElement));

        SelectFormElement genreExpectation = genreExpectation(null).build();
        assertTrue(assertFormElement(fieldset, genreExpectation));
    }

    private SelectFormElement.Builder genreExpectation(BiConsumer<Genre, Option.Builder> builderConsumer) {
        List<Option> options = new ArrayList<>();

        Option.Builder musicBuilder = Option.builder().label(new SimpleMessage("genre.music", "Music")).value("MUSIC");
        if (builderConsumer != null) {
            builderConsumer.accept(Genre.MUSIC, musicBuilder);
        }
        options.add(musicBuilder.build());

        Option.Builder sportBuilder = Option.builder().label(new SimpleMessage("genre.sport", "Sport")).value("SPORT");
        if (builderConsumer != null) {
            builderConsumer.accept(Genre.SPORT, sportBuilder);
        }
        options.add(sportBuilder.build());

        Option.Builder theaterBuilder = Option.builder().label(new SimpleMessage("genre.theater", "Theater")).value("THEATER");
        if (builderConsumer != null) {
            builderConsumer.accept(Genre.THEATER, theaterBuilder);
        }
        options.add(theaterBuilder.build());

        return SelectFormElement.builder()
            .required(true)
            .id("genre")
            .name("genre")
            .label(new SimpleMessage("eventcreateform.genre", "Genre"))
            .options(options);
    }
}

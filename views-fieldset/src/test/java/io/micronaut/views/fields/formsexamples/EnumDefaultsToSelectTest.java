package io.micronaut.views.fields.formsexamples;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.elements.InputTextFormElement;
import io.micronaut.views.fields.elements.Option;
import io.micronaut.views.fields.elements.SelectFormElement;
import io.micronaut.views.fields.messages.SimpleMessage;
import io.micronaut.views.fields.Fieldset;
import io.micronaut.views.fields.FieldsetGenerator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static io.micronaut.views.fields.TestUtils.assertAnyInstance;
import static io.micronaut.views.fields.TestUtils.assertAnyMatch;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        assertEquals(2, fieldset.fields().size());
        assertAnyInstance(fieldset.fields(), InputTextFormElement.class);
        assertAnyInstance(fieldset.fields(), SelectFormElement.class);

        SelectFormElement genreExpectation = genreExpectation(null).build();
        assertAnyMatch(fieldset, genreExpectation);
    }

    private SelectFormElement.Builder genreExpectation(BiConsumer<Genre, Option.Builder> builderConsumer) {
        List<Option> options = new ArrayList<>();

        Option.Builder musicBuilder = Option.builder().label(new SimpleMessage( "Music", "genre.music")).value("MUSIC");
        if (builderConsumer != null) {
            builderConsumer.accept(Genre.MUSIC, musicBuilder);
        }
        options.add(musicBuilder.build());

        Option.Builder sportBuilder = Option.builder().label(new SimpleMessage( "Sport", "genre.sport")).value("SPORT");
        if (builderConsumer != null) {
            builderConsumer.accept(Genre.SPORT, sportBuilder);
        }
        options.add(sportBuilder.build());

        Option.Builder theaterBuilder = Option.builder().label(new SimpleMessage( "Theater", "genre.theater")).value("THEATER");
        if (builderConsumer != null) {
            builderConsumer.accept(Genre.THEATER, theaterBuilder);
        }
        options.add(theaterBuilder.build());

        return SelectFormElement.builder()
            .required(true)
            .id("genre")
            .name("genre")
            .label(new SimpleMessage("Genre", "eventcreateform.genre"))
            .options(options);
    }
}

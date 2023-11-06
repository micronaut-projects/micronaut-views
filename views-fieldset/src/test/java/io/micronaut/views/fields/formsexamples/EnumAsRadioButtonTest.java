package io.micronaut.views.fields.formsexamples;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.Fieldset;
import io.micronaut.views.fields.FieldsetGenerator;
import io.micronaut.views.fields.InputRadioFormElement;
import io.micronaut.views.fields.InputTextFormElement;
import io.micronaut.views.fields.Message;
import io.micronaut.views.fields.Radio;
import io.micronaut.views.fields.SimpleMessage;
import io.micronaut.views.fields.annotations.InputRadio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static io.micronaut.views.fields.TestUtils.assertAnyInstance;
import static io.micronaut.views.fields.formsexamples.FormElementFixture.assertFormElement;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(startApplication = false)
class EnumAsRadioButtonTest {
    @Introspected
    record EventCreateForm(
        @NotBlank String name,
        @InputRadio @NotNull Genre genre) {
    }

    @Test
    void renderEnumAsRadioButton(FieldsetGenerator fieldsetGenerator) {
        Fieldset fieldset = fieldsetGenerator.generate(EventCreateForm.class);
        assertNotNull(fieldset);
        assertEquals(2, fieldset.fields().size());
        assertAnyInstance(fieldset.fields(), InputTextFormElement.class);
        assertAnyInstance(fieldset.fields(), InputRadioFormElement.class);

        InputRadioFormElement genreExpectation = genreExpectation().build();
        assertTrue(assertFormElement(fieldset, genreExpectation));

        fieldset = fieldsetGenerator.generate(new EventCreateForm("Marathon", Genre.SPORT));
        assertNotNull(fieldset);
        genreExpectation = genreExpectationSportChecked().build();
        assertTrue(assertFormElement(fieldset, genreExpectation));
    }

    private InputRadioFormElement.Builder genreExpectation() {
        return InputRadioFormElement.builder().required(true).name("genre").label(Message.of("Genre", "eventcreateform.genre")).buttons(Arrays.asList(
            Radio.builder().label(new SimpleMessage( "Music", "genre.music")).value("MUSIC").id("music").build(),
            Radio.builder().label(new SimpleMessage("Sport", "genre.sport")).value("SPORT").id("sport").build(),
            Radio.builder().label(new SimpleMessage( "Theater", "genre.theater")).value("THEATER").id("theater").build()
        ));
    }

    private InputRadioFormElement.Builder genreExpectationSportChecked() {
        return InputRadioFormElement.builder().required(true).name("genre").label(Message.of("Genre", "eventcreateform.genre")).buttons(Arrays.asList(
            Radio.builder().label(new SimpleMessage("Music", "genre.music")).value("MUSIC").id("music").build(),
            Radio.builder().label(new SimpleMessage("Sport", "genre.sport")).value("SPORT").id("sport").checked(true).build(),
            Radio.builder().label(new SimpleMessage("Theater", "genre.theater")).value("THEATER").id("theater").build()
        ));
    }
}

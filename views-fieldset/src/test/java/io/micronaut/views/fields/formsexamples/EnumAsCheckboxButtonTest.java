package io.micronaut.views.fields.formsexamples;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.*;
import io.micronaut.views.fields.annotations.InputCheckbox;
import io.micronaut.views.fields.annotations.InputRadio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static io.micronaut.views.fields.formsexamples.FormElementFixture.assertFormElement;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class EnumAsCheckboxButtonTest {
    @Introspected
    record EventCreateForm(
        @NotBlank String name,
        @InputCheckbox @NotNull Genre genre) {
    }

    @Test
    void renderEnumAsCheckboxButton(FieldsetGenerator fieldsetGenerator) {
        Fieldset fieldset = fieldsetGenerator.generate(EventCreateForm.class);
        assertNotNull(fieldset);
        assertEquals(2, fieldset.fields().size());
        assertTrue(fieldset.fields().stream().anyMatch(formElement -> formElement instanceof InputTextFormElement));
        assertTrue(fieldset.fields().stream().anyMatch(formElement -> formElement instanceof InputCheckboxFormElement));

        InputCheckboxFormElement genreExpectation = genreExpectation().build();
        assertTrue(assertFormElement(fieldset, genreExpectation));

        fieldset = fieldsetGenerator.generate(new EventCreateForm("Marathon", Genre.SPORT));
        assertNotNull(fieldset);
        genreExpectation = genreExpectationSportChecked().build();
        assertTrue(assertFormElement(fieldset, genreExpectation));
    }

    private InputCheckboxFormElement.Builder genreExpectation() {
        return InputCheckboxFormElement.builder()
            .label(Message.of("Genre", "eventcreateform.genre"))
            .checkboxes(Arrays.asList(
            Checkbox.builder().label(new SimpleMessage( "Music", "genre.music")).value("MUSIC").id("music").build(),
            Checkbox.builder().label(new SimpleMessage("Sport", "genre.sport")).value("SPORT").id("sport").build(),
            Checkbox.builder().label(new SimpleMessage("Theater", "genre.theater")).value("THEATER").id("theater").build()
        ));
    }

    private InputCheckboxFormElement.Builder genreExpectationSportChecked() {
        return InputCheckboxFormElement.builder()
            .label(Message.of("Genre", "eventcreateform.genre"))
            .checkboxes(Arrays.asList(
            Checkbox.builder().label(new SimpleMessage( "Music", "genre.music")).value("MUSIC").id("music").build(),
            Checkbox.builder().label(new SimpleMessage("Sport", "genre.sport")).value("SPORT").id("sport").checked(true).build(),
            Checkbox.builder().label(new SimpleMessage("Theater", "genre.theater")).value("THEATER").id("theater").build()
        ));
    }
}

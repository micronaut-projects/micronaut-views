package io.micronaut.views.fields.formsexamples;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.elements.Checkbox;
import io.micronaut.views.fields.Fieldset;
import io.micronaut.views.fields.FieldsetGenerator;
import io.micronaut.views.fields.elements.InputCheckboxFormElement;
import io.micronaut.views.fields.elements.InputTextFormElement;
import io.micronaut.views.fields.message.Message;
import io.micronaut.views.fields.message.SimpleMessage;
import io.micronaut.views.fields.annotations.InputCheckbox;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static io.micronaut.views.fields.TestUtils.assertAnyInstance;
import static io.micronaut.views.fields.TestUtils.assertAnyMatch;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        assertAnyInstance(fieldset.fields(), InputTextFormElement.class);
        assertAnyInstance(fieldset.fields(), InputCheckboxFormElement.class);

        InputCheckboxFormElement genreExpectation = genreExpectation().build();
        assertAnyMatch(fieldset, genreExpectation);

        fieldset = fieldsetGenerator.generate(new EventCreateForm("Marathon", Genre.SPORT));
        assertNotNull(fieldset);
        genreExpectation = genreExpectationSportChecked().build();
        assertAnyMatch(fieldset, genreExpectation);
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

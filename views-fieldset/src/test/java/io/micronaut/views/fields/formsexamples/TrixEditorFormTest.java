package io.micronaut.views.fields.formsexamples;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.Fieldset;
import io.micronaut.views.fields.FieldsetGenerator;
import io.micronaut.views.fields.SimpleMessage;
import io.micronaut.views.fields.TrixEditorFormElement;
import io.micronaut.views.fields.annotations.TrixEditor;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static io.micronaut.views.fields.TestUtils.assertAnyInstance;
import static io.micronaut.views.fields.formsexamples.FormElementFixture.assertFormElement;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "spec.name", value = "TrixEditorFormTest")
@MicronautTest(startApplication = false)
class TrixEditorFormTest {

    @Test
    void fieldsetGenerationForTrixEditor(FieldsetGenerator fieldsetGenerator, AnswerSaveValidator validator) {
        Fieldset fieldset = fieldsetGenerator.generate(AnswerSave.class);
        assertNotNull(fieldset);
        assertEquals(1, fieldset.fields().size());
        assertAnyInstance(fieldset.fields(), TrixEditorFormElement.class);
        TrixEditorFormElement formElement = trixEditorExpectation().build();
        assertTrue(assertFormElement(fieldset, formElement));

        String value = "I am doing bla bla bla";
        AnswerSave valid = new AnswerSave(value);
        fieldset = fieldsetGenerator.generate(valid);
        assertNotNull(fieldset);
        assertEquals(1, fieldset.fields().size());
        assertAnyInstance(fieldset.fields(), TrixEditorFormElement.class);
        formElement = trixEditorExpectation().value(value).build();
        assertTrue(assertFormElement(fieldset, formElement));

        AnswerSave invalid = new AnswerSave("");
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> validator.validate(invalid));
        fieldset = fieldsetGenerator.generate(invalid, ex);
        assertNotNull(fieldset);
        assertEquals(1, fieldset.fields().size());
        assertAnyInstance(fieldset.fields(), TrixEditorFormElement.class);

        formElement = trixEditorExpectation().value("").errors(Collections.singletonList(new SimpleMessage("must not be blank", "answersave.content.notblank"))).build();
        assertTrue(assertFormElement(fieldset, formElement));
    }

    private TrixEditorFormElement.Builder trixEditorExpectation() {
        return TrixEditorFormElement.builder().name("content").id("content").label(new SimpleMessage("Content", "answersave.content"));
    }

    @Property(name = "spec.name", value = "TrixEditorFormTest")
    @Singleton
    static class AnswerSaveValidator {
        void validate(@Valid AnswerSave loginForm) {
        }
    }

    @Introspected
    record AnswerSave(@TrixEditor @NotBlank String content) {
    }

}

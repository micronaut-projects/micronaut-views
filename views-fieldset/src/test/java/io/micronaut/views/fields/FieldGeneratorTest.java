package io.micronaut.views.fields;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.elements.InputEmailFormElement;
import io.micronaut.views.fields.elements.InputTextFormElement;
import io.micronaut.views.fields.messages.Message;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Collections;
import java.util.List;

import static io.micronaut.views.fields.TestUtils.assertAnyMatch;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Property(name = "spec.name", value = "FieldGeneratorTest")
@MicronautTest(startApplication = false)
class FieldGeneratorTest {

    @Inject
    FieldsetGenerator fieldsetGenerator;

    @Inject
    ContactValidator contactValidator;

    @Test
    void fieldsGenerationWithConstraintViolationExceptionWithMultipleViolations(FieldsetGenerator fieldGenerator) {
        String email = "a".repeat(300) + "@example.com";
        ContactRecordValidationAnnotations instance = new ContactRecordValidationAnnotations("Sergio", "del Amo", email);
        Executable executable = () -> contactValidator.validate(instance);
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, executable);
        Fieldset fieldset = fieldGenerator.generate(instance, ex);
        assertNotNull(fieldset.errors());
        assertEquals(0, fieldset.errors().size());
        List<? extends FormElement> fields = fieldset.fields();
        assertAnyMatch(
            fields,
            InputEmailFormElement.builder().name("email")
                .id("email")
                .maxLength(255)
                .label(Message.of("Email", "contactrecordvalidationannotations.email"))
                .required(true)
                .value(email)
                .errors(List.of(
                    Message.of("must be a well-formed email address", "contactrecordvalidationannotations.email.email"),
                    Message.of("size must be between 0 and 255", "contactrecordvalidationannotations.email.size")
                ))
                .build());
    }

    @Test
    void itIsPossibleToGenerateAFieldFromAPojo(FieldsetGenerator fieldsetGenerator) {
        Fieldset fieldset = fieldsetGenerator.generate(Contact.class);
        assertNotNull(fieldset.errors());
        assertEquals(0, fieldset.errors().size());

        List<? extends FormElement> fields = fieldset.fields();
        assertNotNull(fields);
        assertEquals(2, fields.size());
        assertAnyMatch(fields, InputTextFormElement.builder()
            .name("firstName")
            .label(Message.of("First Name", "contact.firstName"))
            .id("firstName")
            .required(true)
            .build()
        );
        assertAnyMatch(fields, InputTextFormElement.builder()
            .name("lastName")
            .label(Message.of("Last Name", "contact.lastName"))
            .id("lastName")
            .required(false)
            .build()
        );
    }

    @Test
    void itIsPossibleToGenerateAFieldFromARecord(FieldsetGenerator fieldsetGenerator) {
        Fieldset fieldset = fieldsetGenerator.generate(ContactRecord.class);
        assertNotNull(fieldset.errors());
        assertEquals(0, fieldset.errors().size());
        List<? extends FormElement> fields = fieldset.fields();
        assertNotNull(fields);
        assertEquals(2, fields.size());
        assertAnyMatch(fields, InputTextFormElement.builder()
            .name("firstName")
            .label(Message.of("First Name", "contactrecord.firstName"))
            .id("firstName")
            .required(true)
            .build()
        );
        assertAnyMatch(fields, InputTextFormElement.builder()
            .name("lastName")
            .label(Message.of("Last Name", "contactrecord.lastName"))
            .id("lastName")
            .required(false)
            .build()
        );
    }

    @Test
    void itIsPossibleToGenerateAFieldFromARecordWithValidationAnnotations(FieldsetGenerator fieldGenerator) {
        Fieldset fieldset = fieldGenerator.generate(ContactRecordValidationAnnotations.class);
        assertNotNull(fieldset.errors());
        assertEquals(0, fieldset.errors().size());

        List<? extends FormElement> fields = fieldset.fields();
        assertNotNull(fields);
        assertEquals(3, fields.size());

        assertAnyMatch(fields, InputTextFormElement.builder()
            .name("firstName")
            .label(Message.of("First Name", "contactrecordvalidationannotations.firstName"))
            .id("firstName")
            .required(true)
            .build()
        );

        assertAnyMatch(fields, InputTextFormElement.builder()
            .name("lastName")
            .label(Message.of("Last Name", "contactrecordvalidationannotations.lastName"))
            .id("lastName")
            .required(false)
            .build()
        );

        assertAnyMatch(fields, InputEmailFormElement.builder()
            .name("email")
            .label(Message.of("Email", "contactrecordvalidationannotations.email"))
            .id("email")
            .maxLength(255)
            .required(true)
            .build()
        );
    }

    @Test
    void itIsPossibleToGenerateAFieldFromARecordInstanceWithValidationAnnotations(FieldsetGenerator fieldGenerator) {
        Fieldset fieldset = fieldGenerator.generate(new ContactRecordValidationAnnotations("Sergio", "del Amo", null));
        assertNotNull(fieldset.errors());
        assertEquals(0, fieldset.errors().size());

        List<? extends FormElement> fields = fieldset.fields();
        assertNotNull(fields);
        assertEquals(3, fields.size());

        assertAnyMatch(fields, InputTextFormElement.builder()
            .name("firstName")
            .label(Message.of("First Name", "contactrecordvalidationannotations.firstName"))
            .id("firstName")
            .required(true)
            .value("Sergio")
            .build()
        );
        assertAnyMatch(fields, InputTextFormElement.builder()
            .name("lastName")
            .label(Message.of("Last Name", "contactrecordvalidationannotations.lastName"))
            .id("lastName")

            .required(false)
            .value("del Amo")
            .build()
        );
        assertAnyMatch(fields, InputEmailFormElement.builder()
            .name("email")
            .label(Message.of("Email", "contactrecordvalidationannotations.email"))
            .id("email")
            .maxLength(255)
            .required(true)
            .build()
        );
    }

    @Test
    void fieldsGenerationWithConstraintViolationException(FieldsetGenerator fieldGenerator) {
        ContactRecordValidationAnnotations instance = new ContactRecordValidationAnnotations("Sergio", "del Amo", "notanemail");
        Executable e = () -> contactValidator.validate(instance);
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, e);

        Fieldset fieldset = fieldGenerator.generate(instance, ex);
        assertNotNull(fieldset.errors());
        assertEquals(0, fieldset.errors().size());

        List<? extends FormElement> fields = fieldset.fields();
        assertNotNull(fields);
        assertEquals(3, fields.size());

        assertAnyMatch(fields, InputTextFormElement.builder().name("firstName")
            .label(Message.of("First Name", "contactrecordvalidationannotations.firstName"))
            .id("firstName").required(true).value("Sergio").build()
        );
        assertAnyMatch(fields, InputTextFormElement.builder().name("lastName")
            .label(Message.of("Last Name", "contactrecordvalidationannotations.lastName"))
            .id("lastName").required(false).value("del Amo").build()
        );

        FormElement expectedEmail = InputEmailFormElement.builder().name("email")
            .id("email")
            .maxLength(255)
            .label(Message.of("Email", "contactrecordvalidationannotations.email"))
            .required(true)
            .value("notanemail")
            .errors(Collections.singletonList(Message.of("must be a well-formed email address", "contactrecordvalidationannotations.email.email")))
            .build();
        assertAnyMatch(fields, expectedEmail);
    }

    @Requires(property = "spec.name", value = "FieldGeneratorTest")
    @Singleton
    static class ContactValidator {
        void validate(@Valid ContactRecordValidationAnnotations contact) {

        }
    }

    @Introspected
    record ContactRecordValidationAnnotations(@NonNull String firstName,
                                              String lastName,
                                              @NotBlank @Size(max = 255) @Email String email) {
    }

    @Introspected
    record ContactRecord(@NonNull String firstName, String lastName) {
    }

    @Introspected
    static class Contact {
        @NonNull
        private String firstName;
        private String lastName;

        @NonNull
        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(@NonNull String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }
}

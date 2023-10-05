package io.micronaut.views.fields;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.Test;

@Property(name = "spec.name", value = "FieldGeneratorTest")
@MicronautTest(startApplication = false)
class FieldGeneratorTest  {
    @Inject
    FieldsetGenerator fieldsetGenerator;

    @Inject
    ContactValidator contactValidator;

    @Test
    void fieldsGenerationWithConstraintViolationExceptionWithMultipleViolations() {
//        String email = "a".repeat(300) + "@example.com";
//        ContactRecordValidationAnnotations instance = new ContactRecordValidationAnnotations("Sergio", "del Amo", email);
//        Executable executable = () -> contactValidator.validate(instance);
//        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, executable);
//        Fieldset fieldset = fieldGenerator.generate(instance, ex);
//        assertNotNull(fieldset.getErrors());
//        assertEquals(0, fieldset.getErrors().size());
//        List<InputField> fields = fieldset.getFields();
//        assertTrue(fields.stream().anyMatch(f -> f.equals(InputField.builder().name("email")
//                .id("email")
//                .label(Message.of("contactrecordvalidationannotations.email", "Email"))
//                .type(InputType.EMAIL)
//                .required(true)
//                .value(email)
//                .errors(List.of(new SimpleMessage("contactrecordvalidationannotations.email.size", "size must be between 0 and 255"),
//                    new SimpleMessage("contactrecordvalidationannotations.email.email", "must be a well-formed email address")))
//                .build()) ||
//            f.equals(InputField.builder()
//                .name("email")
//                .id("email")
//                .label(Message.of("contactrecordvalidationannotations.email", "Email"))
//                .type(InputType.EMAIL)
//                .required(true)
//                .value(email)
//                .errors(List.of(
//                    new SimpleMessage("contactrecordvalidationannotations.email.email", "must be a well-formed email address"),
//                    new SimpleMessage("contactrecordvalidationannotations.email.size", "size must be between 0 and 255")))
//                .build())
//        ));
    }

    @Test
    void itIsPossibleToGenerateAFieldFromAPojo() {
//        Fieldset fieldset = fieldGenerator.generate(Contact.class);
//        assertNotNull(fieldset.getErrors());
//        assertEquals(0, fieldset.getErrors().size());
//
//        List<InputField> fields = fieldset.getFields();
//        assertNotNull(fields);
//        assertEquals(2, fields.size());
//        assertTrue(fields.stream().anyMatch(f -> InputField.builder()
//            .name("firstName")
//            .label(Message.of("contact.firstName", "First Name"))
//            .id("firstName")
//            .type(InputType.TEXT)
//            .required(true)
//            .build().equals(f)));
//        assertTrue(fields.stream().anyMatch(f -> InputField.builder()
//            .name("lastName")
//            .label(Message.of("contact.lastName", "Last Name"))
//            .id("lastName")
//            .type(InputType.TEXT)
//            .required(false)
//            .build().equals(f)));
    }

    @Test
    void itIsPossibleToGenerateAFieldFromARecord() {
//        Fieldset fieldset = fieldGenerator.generate(ContactRecord.class);
//        assertNotNull(fieldset.getErrors());
//        assertEquals(0, fieldset.getErrors().size());
//        List<InputField> fields = fieldset.getFields();
//        assertNotNull(fields);
//        assertEquals(2, fields.size());
//        assertTrue(fields.stream().anyMatch(f -> InputField.builder()
//            .name("firstName")
//            .label(Message.of("contactrecord.firstName", "First Name"))
//            .id("firstName")
//            .type(InputType.TEXT)
//            .required(true)
//            .build().equals(f)));
//        assertTrue(fields.stream().anyMatch(f -> InputField.builder()
//            .name("lastName")
//            .label(Message.of("contactrecord.lastName", "Last Name"))
//            .id("lastName")
//            .type(InputType.TEXT)
//            .required(false)
//            .build().equals(f)));
    }

    @Test
    void itIsPossibleToGenerateAFieldFromARecordWithValidationAnnotations() {
//        Fieldset fieldset = fieldGenerator.generate(ContactRecordValidationAnnotations.class);
//        assertNotNull(fieldset.getErrors());
//        assertEquals(0, fieldset.getErrors().size());
//
//        List<InputField> fields = fieldset.getFields();
//        assertNotNull(fields);
//        assertEquals(3, fields.size());
//
//        assertTrue(fields.stream().anyMatch(f -> InputField.builder()
//            .name("firstName")
//            .label(Message.of("contactrecordvalidationannotations.firstName", "First Name"))
//            .id("firstName")
//            .type(InputType.TEXT)
//            .required(true)
//            .build().equals(f)));
//        assertTrue(fields.stream().anyMatch(f -> InputField.builder()
//            .name("lastName")
//            .label(Message.of("contactrecordvalidationannotations.lastName", "Last Name"))
//            .id("lastName")
//            .type(InputType.TEXT)
//            .required(false)
//            .build().equals(f)));
//
//        assertTrue(fields.stream().anyMatch(f -> InputField.builder()
//            .name("email")
//            .label(Message.of("contactrecordvalidationannotations.email", "Email"))
//            .id("email")
//            .type(InputType.EMAIL)
//            .required(true)
//            .build().equals(f)));
    }

    @Test
    void itIsPossibleToGenerateAFieldFromARecordInstanceWithValidationAnnotations() {
//        Fieldset fieldset = fieldGenerator.generate(new ContactRecordValidationAnnotations("Sergio", "del Amo", null));
//        assertNotNull(fieldset.getErrors());
//        assertEquals(0, fieldset.getErrors().size());
//
//        List<InputField> fields = fieldset.getFields();
//        assertNotNull(fields);
//        assertEquals(3, fields.size());
//
//        assertTrue(fields.stream().anyMatch(f -> InputField.builder()
//            .name("firstName")
//            .label(Message.of("contactrecordvalidationannotations.firstName", "First Name"))
//            .id("firstName")
//            .type(InputType.TEXT)
//            .required(true)
//            .value("Sergio")
//            .build().equals(f)));
//        assertTrue(fields.stream().anyMatch(f -> InputField.builder()
//            .name("lastName")
//            .label(Message.of("contactrecordvalidationannotations.lastName", "Last Name"))
//            .id("lastName")
//            .type(InputType.TEXT)
//            .required(false)
//            .value("del Amo")
//            .build().equals(f)));
//
//        assertTrue(fields.stream().anyMatch(f -> InputField.builder()
//            .name("email")
//            .label(Message.of("contactrecordvalidationannotations.email", "Email"))
//            .id("email")
//            .type(InputType.EMAIL)
//            .required(true)
//            .build().equals(f)));
    }

    @Test
    void fieldsGenerationWithConstraintViolationException() {
//        ContactRecordValidationAnnotations instance = new ContactRecordValidationAnnotations("Sergio", "del Amo", "notanemail");
//        Executable e = () -> contactValidator.validate(instance);
//        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, e);
//
//        Fieldset fieldset = fieldGenerator.generate(instance, ex);
//        assertNotNull(fieldset.getErrors());
//        assertEquals(0, fieldset.getErrors().size());
//
//        List<InputField> fields = fieldset.getFields();
//        assertNotNull(fields);
//        assertEquals(3, fields.size());
//
//
//        assertTrue(fields.stream().anyMatch(f -> InputField.builder().name("firstName")
//            .label(Message.of("contactrecordvalidationannotations.firstName", "First Name"))
//            .id("firstName").type(InputType.TEXT).required(true).value("Sergio").build().equals(f)));
//        assertTrue(fields.stream().anyMatch(f -> InputField.builder().name("lastName")
//            .label(Message.of("contactrecordvalidationannotations.lastName", "Last Name"))
//            .id("lastName").type(InputType.TEXT).required(false).value("del Amo").build().equals(f)));
//
//        InputField expectedEmail = InputField.builder().name("email")
//            .id("email")
//            .label(Message.of("contactrecordvalidationannotations.email", "Email"))
//            .type(InputType.EMAIL)
//            .required(true)
//            .value("notanemail")
//            .errors(Collections.singletonList(new SimpleMessage("contactrecordvalidationannotations.email.email", "must be a well-formed email address")))
//            .build();
//        assertTrue(fields.stream().anyMatch(expectedEmail::equals));
    }

    @Requires(property = "spec.name", value = "FieldGeneratorTest")
    @Singleton
    static class ContactValidator {
        void validate(@Valid ContactRecordValidationAnnotations contact) {

        }
    }

    @Introspected
    record ContactRecordValidationAnnotations(@NonNull String firstName, String lastName, @NotBlank @Size(max = 255) @Email String email) {
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

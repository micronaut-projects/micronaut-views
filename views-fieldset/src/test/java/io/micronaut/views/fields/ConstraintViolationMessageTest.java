package io.micronaut.views.fields;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Property(name = "spec.name", value = "ConstraintViolationMessageTest")
@MicronautTest(startApplication = false)
class ConstraintViolationMessageTest {
    @Test
    void annotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(ConstraintViolationMessage.class));
    }

    @Test
    void codeAndDefaultMessage(ContactValidator validator) {
        Contact instance = new Contact("Sergio", "del Amo", "notanemail");
        Executable e = () -> validator.validate(instance);
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, e);
        assertEquals(1, ex.getConstraintViolations().size());
        ConstraintViolation<?> violation = ex.getConstraintViolations().iterator().next();
        Message message = new ConstraintViolationMessage(violation);
        assertEquals("must be a well-formed email address", message.defaultMessage());
        assertEquals("contact.email.email", message.code());

    }

    @Requires(property = "spec.name", value = "ConstraintViolationMessageTest")
    @Singleton
    static class ContactValidator {
        void validate(@Valid Contact contact) {

        }
    }

    @Introspected
    record Contact(@NonNull String firstName, String lastName, @NotBlank @Email String email) {
    }
}

package io.micronaut.views.fields.classlevelvalidation;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.ConstraintViolationMessage;
import io.micronaut.views.fields.FieldGenerator;
import io.micronaut.views.fields.Fieldset;
import io.micronaut.views.fields.SimpleMessage;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

@Property(name = "spec.name", value = "ClassLevelValidationTest")
@MicronautTest
class ClassLevelValidationTest {
    @Inject
    FieldGenerator fieldGenerator;

    @Inject
    UserValidator userValidator;

    @Test
    void classLevelValidation() {
        User instance = new User("sdelamo", "foo", "bar");
        Executable e = () -> userValidator.validate(instance);
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, e);
        Fieldset fieldset = fieldGenerator.generate(instance, ex);
        assertNotNull(fieldset);
        assertNotNull(fieldset.getErrors());
        assertEquals(1, fieldset.getErrors().size());
        assertEquals(new SimpleMessage("user.passwordmatch", "Passwords do not match"), fieldset.getErrors().get(0));
    }


    @Requires(property = "spec.name", value = "ClassLevelValidationTest")
    @Singleton
    static class UserValidator {
        void validate(@Valid User user) {

        }
    }

}

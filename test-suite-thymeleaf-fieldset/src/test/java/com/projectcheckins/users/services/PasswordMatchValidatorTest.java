package com.projectcheckins.users.services;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordMatchValidatorTest {
    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(PasswordMatchValidator.class));
    }

    @Test
    void testMatchingValidation() {
        PasswordMatchValidator validator = new PasswordMatchValidator();
        UserSave valid = new UserSave("sherlock", "elementary", "elementary");
        UserSave invalid = new UserSave("sherlock", "elementary", "bogus");
        assertTrue(validator.isValid(valid, null));
        assertFalse(validator.isValid(invalid, null));
    }
}
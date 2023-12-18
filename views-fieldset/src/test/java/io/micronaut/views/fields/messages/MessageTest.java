package io.micronaut.views.fields.messages;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class MessageTest {

    @Test
    void messageValidation(Validator validator) {
        assertTrue(validator.validate(Message.of("Foo")).isEmpty());
        assertTrue(validator.validate(Message.of("Foo", "foo.code")).isEmpty());
        assertFalse(validator.validate(Message.of("")).isEmpty());
        String msg = null;
        assertFalse(validator.validate(Message.of(msg)).isEmpty());
    }
}
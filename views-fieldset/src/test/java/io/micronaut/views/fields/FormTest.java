package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import io.micronaut.views.fields.elements.InputSubmitFormElement;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class FormTest {
    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Form.class));
    }

    @Test
    void formValidation(Validator validator) {
        Fieldset fieldset = new Fieldset(Collections.singletonList(new InputSubmitFormElement(FormGenerator.SUBMIT)), Collections.emptyList());
        // action cannot be an empty string
        assertFalse(validator.validate(new Form("", "post", fieldset, "application/x-www-form-urlencoded")).isEmpty());
        // action cannot be null
        assertFalse(validator.validate(new Form(null, "post", fieldset, "application/x-www-form-urlencoded")).isEmpty());

        // method cannot be an empty string
        assertFalse(validator.validate(new Form("/foo/bar", "", fieldset, null)).isEmpty());
        // method cannot be null
        assertFalse(validator.validate(new Form("/foo/bar", null, fieldset, null)).isEmpty());
        // method can only be get or post
        assertFalse(validator.validate(new Form("/foo/bar", "put", fieldset, null)).isEmpty());

        //method cannot be get if enctype
        Set<ConstraintViolation<Form>> violations = validator.validate(new Form("/foo/bar", "get", fieldset, "application/x-www-form-urlencoded"));
        assertFalse(violations.isEmpty());
        assertEquals(Collections.singletonList("enctype attribute can be used only if method equals post"), violations.stream().map(ConstraintViolation::getMessage).toList());

        // fieldset cannot be null
        Fieldset invalidFieldset = new Fieldset(Collections.emptyList(), Collections.emptyList());
        assertFalse(validator.validate(new Form("/foo/bar", "post", invalidFieldset, "application/x-www-form-urlencoded")).isEmpty());

        // fieldset must be valid
        assertFalse(validator.validate(new Form("/foo/bar", "post", null, "application/x-www-form-urlencoded")).isEmpty());

        // enctype has to be form-url-enconded form-data or txt
        assertFalse(validator.validate(new Form("/foo/bar", "post", fieldset, "text/html")).isEmpty());

        // enctype can be null
        assertTrue(validator.validate(new Form("/foo/bar", "post", fieldset, null)).isEmpty());
        assertTrue(validator.validate(new Form("/foo/bar", "post", fieldset, "application/x-www-form-urlencoded")).isEmpty());
        assertTrue(validator.validate(new Form("/foo/bar", "post", fieldset, "multipart/form-data")).isEmpty());
        assertTrue(validator.validate(new Form("/foo/bar", "post", fieldset, "text/plain")).isEmpty());
        assertTrue(validator.validate(new Form("/foo/bar", "get", fieldset, null)).isEmpty());

    }
}

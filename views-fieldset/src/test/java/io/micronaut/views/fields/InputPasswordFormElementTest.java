package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InputPasswordFormElementTest {
    private static final String ID = "passwordid";
    private static final String NAME = "password";
    private static final boolean REQUIRED = true;
    private static final boolean READONLY = true;
    private static final Integer SIZE = 10;
    private static final String PLACEHOLDER = "Password";
    private static final Integer MINLENGTH = 4;
    private static final Integer MAXLENGTH = 8;

    @Test
    void builder() {
        InputPasswordFormElement formElement = InputPasswordFormElement.builder()
            .name(NAME)
            .id(ID)
            .placeholder(PLACEHOLDER)
            .required(REQUIRED)
            .readOnly(READONLY)
            .size(SIZE)
            .minLength(MINLENGTH)
            .maxLength(MAXLENGTH)
            .build();
        assertFormElement(formElement);
        BeanIntrospection<InputPasswordFormElement> introspection = BeanIntrospection.getIntrospection(InputPasswordFormElement.class);
        BeanIntrospection.Builder<InputPasswordFormElement> builder = introspection.builder();
        formElement = builder
            .with("name", NAME)
            .with("id", ID)
            .with("placeholder", PLACEHOLDER)
            .with("required", REQUIRED)
            .with("readOnly", READONLY)
            .with("size", SIZE)
            .with("minLength", MINLENGTH)
            .with("maxLength", MAXLENGTH)
            .build();
        assertFormElement(formElement);
    }

    private void assertFormElement(InputPasswordFormElement formElement) {
        assertNotNull(formElement);
        assertEquals(ID, formElement.getId());
        assertEquals(NAME, formElement.getName());
        assertEquals(REQUIRED, formElement.isRequired());
        assertEquals(READONLY, formElement.isReadOnly());
        assertEquals(SIZE, formElement.getSize());
        assertEquals(PLACEHOLDER, formElement.getPlaceholder());
        assertEquals(MINLENGTH, formElement.getMinLength());
        assertEquals(MAXLENGTH, formElement.getMaxLength());
    }
}
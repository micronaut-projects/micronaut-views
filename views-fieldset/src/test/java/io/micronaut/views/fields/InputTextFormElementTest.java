package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputTextFormElementTest {
    private static final String ID = "uname";
    private static final String NAME = "name";
    private static final boolean REQUIRED = true;
    private static final boolean READONLY = true;
    private static final Integer SIZE = 10;
    private static final String PLACEHOLDER = "Username";
    private static final Integer MINLENGTH = 4;
    private static final Integer MAXLENGTH = 8;

    @Test
    void builder() {
        InputTextFormElement formElement = InputTextFormElement.builder()
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
        BeanIntrospection<InputTextFormElement> introspection = BeanIntrospection.getIntrospection(InputTextFormElement.class);
        BeanIntrospection.Builder<InputTextFormElement> builder = introspection.builder();
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

    private void assertFormElement(InputTextFormElement formElement) {
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

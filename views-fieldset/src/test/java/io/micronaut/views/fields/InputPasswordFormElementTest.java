package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.views.fields.elements.InputPasswordFormElement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    void testTagAndType() {
        InputPasswordFormElement formElement = InputPasswordFormElement.builder().build();
        assertEquals(HtmlTag.TAG_INPUT, formElement.getTag());
        assertEquals(InputType.ATTR_TYPE_PASSWORD, formElement.getType());
    }

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
        assertFalse(formElement.hasErrors());
    }

    private void assertFormElement(InputPasswordFormElement formElement) {
        assertNotNull(formElement);
        assertEquals(ID, formElement.id());
        assertEquals(NAME, formElement.name());
        assertEquals(REQUIRED, formElement.required());
        assertEquals(READONLY, formElement.readOnly());
        assertEquals(SIZE, formElement.size());
        assertEquals(PLACEHOLDER, formElement.placeholder());
        assertEquals(MINLENGTH, formElement.minLength());
        assertEquals(MAXLENGTH, formElement.maxLength());
    }
}

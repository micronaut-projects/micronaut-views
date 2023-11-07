package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.views.fields.elements.InputTextFormElement;
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
    void testTagAndType() {
        InputTextFormElement formElement = InputTextFormElement.builder().build();
        assertEquals(HtmlTag.TAG_INPUT, formElement.getTag());
        assertEquals(InputType.ATTR_TYPE_TEXT, formElement.getType());
    }

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

        assertFalse(formElement.hasErrors());
    }

    private void assertFormElement(InputTextFormElement formElement) {
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

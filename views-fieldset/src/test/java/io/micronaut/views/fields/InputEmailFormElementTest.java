package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.views.fields.render.InputType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputEmailFormElementTest {
    private static final String ID = "uname";
    private static final String NAME = "name";
    private static final boolean REQUIRED = true;
    private static final boolean READONLY = true;
    private static final Integer SIZE = 255;
    private static final String PLACEHOLDER = "foo@gmail.com";
    private static final Integer MINLENGTH = 4;
    private static final Integer MAXLENGTH = 30;

    @Test
    void testTagAndType() {
        InputEmailFormElement formElement = InputEmailFormElement.builder().build();
        assertEquals(HtmlTag.TAG_INPUT, formElement.getTag());
        assertEquals(InputType.ATTR_TYPE_EMAIL, formElement.getType());
    }

    @Test
    void builder() {
        InputEmailFormElement formElement = InputEmailFormElement.builder()
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
        BeanIntrospection<InputEmailFormElement> introspection = BeanIntrospection.getIntrospection(InputEmailFormElement.class);
        BeanIntrospection.Builder<InputEmailFormElement> builder = introspection.builder();
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

    private void assertFormElement(InputEmailFormElement formElement) {
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

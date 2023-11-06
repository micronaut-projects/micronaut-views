package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.views.fields.elements.InputTelFormElement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputTelFormElementTest {
    private static final String ID = "tel";
    private static final String NAME = "tel";
    private static final boolean REQUIRED = true;
    private static final boolean READONLY = true;
    private static final Integer SIZE = 255;
    private static final String PLACEHOLDER = "123456789";
    private static final Integer MINLENGTH = 4;
    private static final Integer MAXLENGTH = 30;

    @Test
    void testTagAndType() {
        InputTelFormElement formElement = InputTelFormElement.builder().build();
        assertEquals(HtmlTag.TAG_INPUT, formElement.getTag());
        assertEquals(InputType.ATTR_TYPE_TEL, formElement.getType());
    }

    @Test
    void builder() {
        InputTelFormElement formElement = InputTelFormElement.builder()
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
        BeanIntrospection<InputTelFormElement> introspection = BeanIntrospection.getIntrospection(InputTelFormElement.class);
        BeanIntrospection.Builder<InputTelFormElement> builder = introspection.builder();
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

    private void assertFormElement(InputTelFormElement formElement) {
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

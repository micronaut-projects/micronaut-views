package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.views.fields.render.InputType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputUrlFormElementTest {
    private static final String ID = "url";
    private static final String NAME = "url";
    private static final boolean REQUIRED = true;
    private static final boolean READONLY = true;
    private static final Integer SIZE = 255;
    private static final String PLACEHOLDER = "https://micronaut.io";
    private static final Integer MINLENGTH = 4;
    private static final Integer MAXLENGTH = 30;

    @Test
    void testTagAndType() {
        InputUrlFormElement formElement = InputUrlFormElement.builder().build();
        assertEquals(HtmlTag.TAG_INPUT, formElement.getTag());
        assertEquals(InputType.ATTR_TYPE_URL, formElement.getType());
    }

    @Test
    void builder() {
        InputUrlFormElement formElement = InputUrlFormElement.builder()
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
        BeanIntrospection<InputUrlFormElement> introspection = BeanIntrospection.getIntrospection(InputUrlFormElement.class);
        BeanIntrospection.Builder<InputUrlFormElement> builder = introspection.builder();
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

    private void assertFormElement(InputUrlFormElement formElement) {
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

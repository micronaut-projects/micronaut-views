package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.views.fields.elements.InputNumberFormElement;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InputNumberFormElementTest {
    private  final String ID = "numid";
    private static final String NAME = "num";
    private static final boolean REQUIRED = true;
    private static final boolean READONLY = true;
    private static final Integer SIZE = 10;
    private static final String PLACEHOLDER = "multiple of 10";
    private static final Integer MIN = 0;
    private static final Integer MAX = 100;
    private static final String STEP = "10";

    @Test
    void testTagAndType() {
        InputNumberFormElement formElement = InputNumberFormElement.builder().build();
        assertEquals(HtmlTag.INPUT, formElement.getTag());
        assertEquals(InputType.NUMBER, formElement.getType());
    }

    @Test
    void builder() {
        InputNumberFormElement formElement = InputNumberFormElement.builder()
            .name(NAME)
            .id(ID)
            .placeholder(PLACEHOLDER)
            .required(REQUIRED)
            .readOnly(READONLY)
            .min(MIN)
            .max(MAX)
            .step(STEP)
            .build();
        assertFormElement(formElement);
        BeanIntrospection<InputNumberFormElement> introspection = BeanIntrospection.getIntrospection(InputNumberFormElement.class);
        BeanIntrospection.Builder<InputNumberFormElement> builder = introspection.builder();
        formElement = builder
            .with("name", NAME)
            .with("id", ID)
            .with("placeholder", PLACEHOLDER)
            .with("required", REQUIRED)
            .with("readOnly", READONLY)
            .with("size", SIZE)
            .with("min", MIN)
            .with("max", MAX)
            .with("step", STEP)
            .build();
        assertFormElement(formElement);

        assertFalse(formElement.hasErrors());
    }

    private void assertFormElement(InputNumberFormElement formElement) {
        assertNotNull(formElement);
        assertEquals(ID, formElement.id());
        assertEquals(NAME, formElement.name());
        assertEquals(REQUIRED, formElement.required());
        assertEquals(READONLY, formElement.readOnly());
        assertEquals(PLACEHOLDER, formElement.placeholder());
        assertEquals(MIN, formElement.min());
        assertEquals(MAX, formElement.max());
        assertEquals(STEP, formElement.step());
    }

}

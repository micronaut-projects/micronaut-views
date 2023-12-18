package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.views.fields.elements.InputFileFormElement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputFileFormElementTest {
    private  final String ID = "numid";
    private static final String NAME = "num";
    private static final boolean REQUIRED = true;
    private static final String ACCEPT = ".doc,.docx,.xml,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    @Test
    void testTagAndType() {
        InputFileFormElement formElement = InputFileFormElement.builder().build();
        assertEquals(HtmlTag.INPUT, formElement.getTag());
        assertEquals(InputType.FILE, formElement.getType());
    }

    @Test
    void builder() {
        InputFileFormElement formElement = InputFileFormElement.builder()
                .name(NAME)
                .id(ID)
                .accept(ACCEPT)
                .required(REQUIRED)
                .build();
        assertFormElement(formElement);
        BeanIntrospection<InputFileFormElement> introspection = BeanIntrospection.getIntrospection(InputFileFormElement.class);
        BeanIntrospection.Builder<InputFileFormElement> builder = introspection.builder();
        formElement = builder
                .with("name", NAME)
                .with("id", ID)
                .with("accept", ACCEPT)
                .with("required", REQUIRED)
                .build();
        assertFormElement(formElement);
        assertFalse(formElement.hasErrors());
    }

    private void assertFormElement(InputFileFormElement formElement) {
        assertNotNull(formElement);
        assertEquals(ID, formElement.id());
        assertEquals(NAME, formElement.name());
        assertEquals(REQUIRED, formElement.required());
        assertEquals(ACCEPT, formElement.accept());
    }
}
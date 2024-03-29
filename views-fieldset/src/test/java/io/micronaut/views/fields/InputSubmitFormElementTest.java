package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.views.fields.elements.InputSubmitFormElement;
import io.micronaut.views.fields.messages.Message;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputSubmitFormElementTest {
    @Test
    void testTagAndType() {
        InputSubmitFormElement formElement = InputSubmitFormElement.builder().build();
        assertEquals(HtmlTag.INPUT, formElement.getTag());
        assertEquals(InputType.SUBMIT, formElement.getType());
    }

    @Test
    void builder() {
        Message value = Message.of("Send Request");
        InputSubmitFormElement formElement = InputSubmitFormElement.builder()
            .value(value)
            .build();
        assertNotNull(formElement);
        assertEquals(value, formElement.value());
        BeanIntrospection<InputSubmitFormElement> introspection = BeanIntrospection.getIntrospection(InputSubmitFormElement.class);
        BeanIntrospection.Builder<InputSubmitFormElement> builder = introspection.builder();
        formElement = builder
            .with("value", value)
            .build();
        assertNotNull(formElement);
        assertEquals(value, formElement.value());
    }
}

package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputSubmitFormElementTest {
    @Test
    void builder() {
        Message value = Message.of("Send Request", null);
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

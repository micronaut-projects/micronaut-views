package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.views.fields.elements.InputHiddenFormElement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InputHiddenFormElementTest {

    @Test
    void testTagAndType() {
        InputHiddenFormElement formElement = InputHiddenFormElement.builder().build();
        assertEquals(HtmlTag.INPUT, formElement.getTag());
        assertEquals(InputType.HIDDEN, formElement.getType());
    }

    @Test
    void builder() {
        String name = "postId";
        String value = "34657";
        InputHiddenFormElement formElement = InputHiddenFormElement.builder()
            .name(name)
            .value(value)
            .build();
        assertFormElement(name, value, formElement);
        BeanIntrospection<InputHiddenFormElement> introspection = BeanIntrospection.getIntrospection(InputHiddenFormElement.class);
        BeanIntrospection.Builder<InputHiddenFormElement> builder = introspection.builder();
        formElement = builder
            .with("name", name)
            .with("value", value)
            .build();
        assertFormElement(name, value, formElement);
    }

    private void assertFormElement(String name,
                                   String value,
                                   InputHiddenFormElement formElement) {
        assertNotNull(formElement);
        assertEquals(name, formElement.name());
        assertEquals(value, formElement.value());
    }
}

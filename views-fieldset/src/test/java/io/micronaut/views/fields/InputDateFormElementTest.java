package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.views.fields.render.InputType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InputDateFormElementTest {
    @Test
    void testTagAndType() {
        InputDateFormElement formElement = InputDateFormElement.builder().build();
        assertEquals(HtmlTag.TAG_INPUT, formElement.getTag());
        assertEquals(InputType.ATTR_TYPE_DATE, formElement.getType());
    }

    @Test
    void inputDateFormElementOffersBuilderApi() {
        String id = "start";
        String name = "trip-start";
        LocalDate value = LocalDate.of(2018, 7, 22);
        LocalDate min = LocalDate.of(2018, 1, 1);
        LocalDate max = LocalDate.of(2018, 12, 31);

        InputDateFormElement formElement = InputDateFormElement.builder()
            .id(id)
            .name(name)
            .value(value)
            .min(min)
            .max(max)
            .build();
        assertNotNull(formElement);
        assertEquals(id, formElement.id());
        assertEquals(name, formElement.name());
        assertEquals(value, formElement.value());
        assertEquals(min, formElement.min());
        assertEquals(max, formElement.max());

        BeanIntrospection<InputDateFormElement> introspection = BeanIntrospection.getIntrospection(InputDateFormElement.class);
        BeanIntrospection.Builder<InputDateFormElement> builder = introspection.builder();
        formElement = builder
            .with("id", id)
            .with("name", name)
            .with("value", value)
            .with("min", min)
            .with("max", max)
            .build();
        assertNotNull(formElement);
        assertEquals(id, formElement.id());
        assertEquals(name, formElement.name());
        assertEquals(value, formElement.value());
        assertEquals(min, formElement.min());
        assertEquals(max, formElement.max());

        assertFalse(formElement.hasErrors());
    }
}

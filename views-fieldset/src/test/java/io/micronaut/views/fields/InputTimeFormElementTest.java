package io.micronaut.views.fields;

import io.micronaut.views.fields.elements.InputTimeFormElement;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InputTimeFormElementTest {
    @Test
    void testTagAndType() {
        InputTimeFormElement formElement = InputTimeFormElement.builder().build();
        assertEquals(HtmlTag.INPUT, formElement.getTag());
        assertEquals(InputType.TIME, formElement.getType());
    }

    @Test
    void builder() {
        String id = "apptid";
        String name = "appt";
        LocalTime value = LocalTime.of(10, 0);

        LocalTime min = LocalTime.of(9, 0);

        LocalTime max = LocalTime.of(18, 0);


        InputTimeFormElement formElement = InputTimeFormElement.builder()
            .name(name)
            .id(id)
            .value(value)
            .min(min)
            .max(max)
            .build();

        assertNotNull(formElement);
        assertEquals(id, formElement.id());
        assertFalse(formElement.required());
        assertEquals(name, formElement.name());
        assertEquals("10:00", formElement.value().toString());
        assertEquals("09:00", formElement.min().toString());
        assertEquals("18:00", formElement.max().toString());

        assertFalse(formElement.hasErrors());
    }

}

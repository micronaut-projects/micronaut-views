package io.micronaut.views.fields;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class InputTimeFormElementTest {
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
    }

}

package io.micronaut.views.fields;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InputDataTimeLocalFormElementTest {
    @Test
    void builder() {
        String id = "id-meeting-time";
        String name = "meeting-time";
        LocalDateTime value = LocalDateTime.of(2018, 6, 12, 19, 30);

        LocalDateTime min = LocalDateTime.of(2018, 6, 7, 0, 0);

        LocalDateTime max = LocalDateTime.of(2018, 6, 14, 0, 0);

        InputDateTimeLocalFormElement formElement = InputDateTimeLocalFormElement.builder()
            .name(name)
            .id(id)
            .value(value)
            .min(min)
            .max(max)
            .build();

        assertNotNull(formElement);
        assertEquals(id, formElement.getId());
        assertFalse(formElement.isRequired());
        assertEquals(name, formElement.getName());
        assertEquals("2018-06-12T19:30", formElement.getValue().toString());
        assertEquals("2018-06-07T00:00", formElement.getMin().toString());
        assertEquals("2018-06-14T00:00", formElement.getMax().toString());
    }

}

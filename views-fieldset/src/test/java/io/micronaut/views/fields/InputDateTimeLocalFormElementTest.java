package io.micronaut.views.fields;

import io.micronaut.views.fields.elements.InputDateTimeLocalFormElement;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InputDateTimeLocalFormElementTest {

    @Test
    void testTagAndType() {
        InputDateTimeLocalFormElement formElement = InputDateTimeLocalFormElement.builder().build();
        assertEquals(HtmlTag.TAG_INPUT, formElement.getTag());
        assertEquals(InputType.ATTR_TYPE_DATE_TIME_LOCAL, formElement.getType());
    }

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
        assertEquals(id, formElement.id());
        assertFalse(formElement.required());
        assertEquals(name, formElement.name());
        assertEquals("2018-06-12T19:30", formElement.value().toString());
        assertEquals("2018-06-07T00:00", formElement.min().toString());
        assertEquals("2018-06-14T00:00", formElement.max().toString());

        assertFalse(formElement.hasErrors());
    }
}
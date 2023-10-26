package io.micronaut.views.fields;

import io.micronaut.views.fields.render.InputType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputDateTimeLocalFormElementTest {

    @Test
    void testTagAndType() {
        InputDateTimeLocalFormElement formElement = InputDateTimeLocalFormElement.builder().build();
        assertEquals(HtmlTag.TAG_INPUT, formElement.getTag());
        assertEquals(InputType.ATTR_TYPE_DATE_TIME_LOCAL, formElement.getType());
    }
}
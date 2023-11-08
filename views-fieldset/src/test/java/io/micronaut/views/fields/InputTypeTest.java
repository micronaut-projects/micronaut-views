package io.micronaut.views.fields;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputTypeTest {
    @Test
    void toStringReturnsType() {
        assertEquals("radio", InputType.RADIO.toString());
        assertEquals("submit", InputType.SUBMIT.toString());
        assertEquals("hidden", InputType.HIDDEN.toString());
        assertEquals("number", InputType.NUMBER.toString());
        assertEquals("date", InputType.DATE.toString());
        assertEquals("time", InputType.TIME.toString());
        assertEquals("datetime-local", InputType.DATE_TIME_LOCAL.toString());
        assertEquals("checkbox", InputType.CHECKBOX.toString());
        assertEquals("email", InputType.EMAIL.toString());
        assertEquals("password", InputType.PASSWORD.toString());
        assertEquals("url", InputType.URL.toString());
        assertEquals("text", InputType.TEXT.toString());
        assertEquals("tel", InputType.TEL.toString());
    }
}
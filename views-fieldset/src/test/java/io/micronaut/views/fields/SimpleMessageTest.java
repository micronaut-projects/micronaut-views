package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.views.fields.messages.SimpleMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleMessageTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(SimpleMessage.class));
    }

    @Test
    void simpleMessageOverrideEqualsAndHashCode() {
        assertEquals(new SimpleMessage("Title", "book.title"),
            new SimpleMessage("Title", "book.title"));

        assertEquals(new SimpleMessage("Title", null),
            new SimpleMessage("Title", null));
    }
}

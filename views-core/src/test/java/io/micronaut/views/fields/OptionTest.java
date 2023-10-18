package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OptionTest {
    @Test
    void builder() {
        Option option = Option.builder().value("MUSIC").label(new SimpleMessage( "Music", "genre.music")).build();
        assertNotNull(option);
        assertEquals("MUSIC", option.getValue());
        assertEquals("genre.music", option.getLabel().getCode());
        assertEquals("Music", option.getLabel().getDefaultMessage());
    }
}

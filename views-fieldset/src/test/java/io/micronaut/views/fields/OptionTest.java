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
        assertEquals("genre.music", option.getLabel().code());
        assertEquals("Music", option.getLabel().defaultMessage());

        BeanIntrospection<Option> introspection = BeanIntrospection.getIntrospection(Option.class);
        BeanIntrospection.Builder<Option> builder = introspection.builder();
        option = builder
            .with("value", "MUSIC")
            .with("label", new SimpleMessage( "Music", "genre.music"))
            .build();

        assertEquals("MUSIC", option.getValue());
        assertEquals("Music", option.getLabel().defaultMessage());
    }
}

package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.views.fields.elements.Option;
import io.micronaut.views.fields.messages.SimpleMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OptionTest {
    @Test
    void testTag() {
        Option formElement = Option.builder().build();
        assertEquals(HtmlTag.TAG_OPTION, formElement.getTag());
    }

    @Test
    void builder() {
        Option option = Option.builder().value("MUSIC").label(new SimpleMessage( "Music", "genre.music")).build();

        assertNotNull(option);
        assertEquals("MUSIC", option.value());
        assertEquals("genre.music", option.label().code());
        assertEquals("Music", option.label().defaultMessage());

        BeanIntrospection<Option> introspection = BeanIntrospection.getIntrospection(Option.class);
        BeanIntrospection.Builder<Option> builder = introspection.builder();
        option = builder
            .with("value", "MUSIC")
            .with("label", new SimpleMessage( "Music", "genre.music"))
            .build();

        assertEquals("MUSIC", option.value());
        assertEquals("Music", option.label().defaultMessage());
    }
}

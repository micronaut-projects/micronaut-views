package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RadioTest {

    @Test
    void radioHasABuilderApi() {
        String value = "value";
        String id = "id";
        Message label = Message.of("drone.huey", "Huey");
        Radio radio = Radio.builder()
            .value(value)
            .id(id)
            .label(label)
            .build();
        assertEquals(id, radio.getId());
        assertEquals(label, radio.getLabel());
        assertEquals(value, radio.getValue());

        BeanIntrospection<Radio> introspection = BeanIntrospection.getIntrospection(Radio.class);
        BeanIntrospection.Builder<Radio> builder = introspection.builder();
        radio = builder
            .with("id", id)
            .with("value", value)
            .with("label", label)
            .build();

        assertEquals(id, radio.getId());
        assertEquals(label, radio.getLabel());
        assertEquals(value, radio.getValue());
    }
}

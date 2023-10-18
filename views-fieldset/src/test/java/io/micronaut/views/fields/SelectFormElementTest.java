package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SelectFormElementTest {
    @Test
    void selectFormHasABuilderApi() {
        String name = "cars";
        String id = "carsid";
        List<Option> options = Arrays.asList(
                Option.builder().value("volvo").label(new SimpleMessage("Volvo", "car.volvo")).build(),
                Option.builder().value("saab").label(new SimpleMessage("Saab", "car.saab")).build(),
                Option.builder().value("mercedes").label(new SimpleMessage("Mercedes", "car.mercedes")).build(),
                Option.builder().value("audi").label(new SimpleMessage("Audi", "car.audi")).build()
        );
        SelectFormElement selectFormElement = SelectFormElement.builder()
            .name(name)
            .id(id)
            .options(options)
            .build();
        assetSelectFormElement(name, id, options, selectFormElement);

        BeanIntrospection<SelectFormElement> introspection = BeanIntrospection.getIntrospection(SelectFormElement.class);
        BeanIntrospection.Builder<SelectFormElement> builder = introspection.builder();
        SelectFormElement form = builder
            .with("name", name)
            .with("id", id)
            .with("options", options)
            .build();
        assetSelectFormElement(name, id, options, selectFormElement);
    }

    private void assetSelectFormElement( String name,
                                         String id,
                                         List<Option> options,
                                         SelectFormElement selectFormElement) {
        assertNotNull(selectFormElement);
        assertEquals(name, selectFormElement.name());
        assertEquals(id, selectFormElement.id());
        assertNotNull(selectFormElement.options());
        assertEquals(4, selectFormElement.options().size());
    }
}

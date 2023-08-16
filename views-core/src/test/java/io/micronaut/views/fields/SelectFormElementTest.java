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
            new Option("Volvo", "volvo", false, false, null),
            new Option("Saab", "saab", false, false, null),
            new Option("Mercedes", "mercedes", false, false, null),
            new Option("Audi", "audi", false, false, null)
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
        assertEquals(name, selectFormElement.getName());
        assertEquals(id, selectFormElement.getId());
        assertNotNull(selectFormElement.getOptions());
        assertEquals(4, selectFormElement.getOptions().size());
    }
}

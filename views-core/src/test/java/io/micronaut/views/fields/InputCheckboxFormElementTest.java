package io.micronaut.views.fields;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InputCheckboxFormElementTest {

    @Test
    void builder() {
        Checkbox interest = new Checkbox("interest", "coding", false, "coding", new SimpleMessage("interest.coding", "Coding"));
        Checkbox music = new Checkbox("interest", "music", false, "music", new SimpleMessage("interest.music", "Music"));

        InputCheckboxFormElement formElement = InputCheckboxFormElement.builder()
            .checkbox(interest)
            .checkbox(music)
            .build();
        List<Checkbox> checkboxes = Arrays.asList(interest, music);
        assertFormElement(checkboxes, formElement);
        BeanIntrospection<InputCheckboxFormElement> introspection = BeanIntrospection.getIntrospection(InputCheckboxFormElement.class);
        BeanIntrospection.Builder<InputCheckboxFormElement> builder = introspection.builder();
        formElement = builder
            .with("checkboxes", checkboxes)
            .build();
        assertFormElement(checkboxes, formElement);
    }

    private void assertFormElement(List<Checkbox> checkboxes,
                                   InputCheckboxFormElement formElement) {
        assertNotNull(formElement);
        assertEquals(checkboxes.size(), formElement.getCheckboxes().size());
    }
}

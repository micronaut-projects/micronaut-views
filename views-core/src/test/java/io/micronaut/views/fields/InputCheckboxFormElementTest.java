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
        Checkbox interest = Checkbox.builder().name("interest").value("coding").checked(false).id("coding").label(new SimpleMessage("interest.coding", "Coding")).build();
        Checkbox music = Checkbox.builder().name("interest").value("music").checked(false).id("music").label(new SimpleMessage("interest.music", "Coding")).build();

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

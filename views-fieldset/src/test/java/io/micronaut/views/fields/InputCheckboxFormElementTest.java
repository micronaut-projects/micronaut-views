package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InputCheckboxFormElementTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(InputCheckboxFormElement.class));
    }

    @Test
    void builder() {
        Checkbox interest = Checkbox.builder().name("interest").value("coding").checked(false).id("coding").label(new SimpleMessage("Coding", "interest.coding")).build();
        Checkbox music = Checkbox.builder().name("interest").value("music").checked(false).id("music").label(new SimpleMessage("Coding", "interest.music")).build();

        InputCheckboxFormElement formElement = InputCheckboxFormElement.builder()
            .checkboxes(List.of(interest, music))
            .build();
        List<Checkbox> checkboxes = Arrays.asList(interest, music);
        assertFormElement(checkboxes, formElement);
        BeanIntrospection<InputCheckboxFormElement> introspection = BeanIntrospection.getIntrospection(InputCheckboxFormElement.class);
        BeanIntrospection.Builder<InputCheckboxFormElement> builder = introspection.builder();
        formElement = builder
            .with("checkboxes", checkboxes)
            .build();
        assertFormElement(checkboxes, formElement);
        assertFalse(formElement.hasErrors());
    }

    private void assertFormElement(List<Checkbox> checkboxes,
                                   InputCheckboxFormElement formElement) {
        assertNotNull(formElement);
        assertEquals(checkboxes.size(), formElement.checkboxes().size());
    }
}

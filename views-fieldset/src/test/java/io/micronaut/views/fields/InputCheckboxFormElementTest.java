package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.views.fields.elements.Checkbox;
import io.micronaut.views.fields.elements.InputCheckboxFormElement;
import io.micronaut.views.fields.messages.Message;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InputCheckboxFormElementTest {
    @Test
    void testTagAndType() {
        InputCheckboxFormElement formElement = InputCheckboxFormElement.builder().build();
        assertEquals(HtmlTag.INPUT, formElement.getTag());
        assertEquals(InputType.ATTR_TYPE_CHECKBOX, formElement.getType());
    }

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(InputCheckboxFormElement.class));
    }

    @Test
    void builder() {
        Checkbox interest = Checkbox.builder().name("interest").value("coding").checked(false).id("coding").label(Message.of("Coding", "interest.coding")).build();
        Checkbox music = Checkbox.builder().name("interest").value("music").checked(false).id("music").label(Message.of("Coding", "interest.music")).build();

        InputCheckboxFormElement formElement = InputCheckboxFormElement.builder()
            .checkboxes(List.of(interest, music))
            .build();
        assertEquals(HtmlTag.INPUT, formElement.getTag());
        assertEquals(InputType.ATTR_TYPE_CHECKBOX, formElement.getType());

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

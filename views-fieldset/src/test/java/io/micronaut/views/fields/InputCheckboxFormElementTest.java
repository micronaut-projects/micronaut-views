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
        assertEquals(InputType.CHECKBOX, formElement.getType());
    }

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(InputCheckboxFormElement.class));
    }

    @Test
    void builder() {
        Checkbox coding = Checkbox.builder().name("interest").value("coding").checked(false).id("coding").label(Message.of("Coding", "interest.coding")).build();
        Checkbox music = Checkbox.builder().name("interest").value("music").checked(true).id("music").label(Message.of("Music", "interest.music")).build();
        Checkbox cookery = Checkbox.builder().name("interest").value("cookery").disabled(true).checked(false).id("cookery").label(Message.of("Cookery", "interest.cookery")).build();

        assertFalse(coding.checked(), "Coding checkbox should not be checked");
        assertTrue(music.checked(), "Music checkbox should be checked");
        assertFalse(cookery.checked(), "Cookery checkbox should not be checked");

        assertFalse(coding.disabled(), "Coding checkbox should not be disabled");
        assertFalse(music.disabled(), "Music checkbox should not be disabled");
        assertTrue(cookery.disabled(), "Cookery checkbox should be disabled");

        InputCheckboxFormElement formElement = InputCheckboxFormElement.builder()
            .checkboxes(List.of(coding, music, cookery))
            .build();
        assertEquals(HtmlTag.INPUT, formElement.getTag());
        assertEquals(InputType.CHECKBOX, formElement.getType());

        List<Checkbox> checkboxes = Arrays.asList(coding, music, cookery);
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

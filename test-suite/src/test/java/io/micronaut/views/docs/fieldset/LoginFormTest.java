package io.micronaut.views.docs.fieldset;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.Form;
import io.micronaut.views.fields.FormGenerator;
import io.micronaut.views.fields.elements.InputPasswordFormElement;
import io.micronaut.views.fields.elements.InputTextFormElement;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@MicronautTest(startApplication = false)
class LoginFormTest {

    @Inject
    FormGenerator formGenerator;

    @Test
    void thePasswordFieldIsRenderedAsAPasswordInput() {
        Form form = formGenerator.generate("/login", Login.class);
        assertEquals("/login", form.action());
        assertEquals("post", form.method());
        assertEquals(3, form.fieldset().fields().size());
        InputTextFormElement username = assertInstanceOf(InputTextFormElement.class, form.fieldset().fields().get(0));
        assertEquals("username", username.name());
        assertEquals(true, username.required());
        InputPasswordFormElement password = assertInstanceOf(InputPasswordFormElement.class, form.fieldset().fields().get(1));
        assertEquals("password", password.name());
        assertEquals(true, password.required());
    }
}

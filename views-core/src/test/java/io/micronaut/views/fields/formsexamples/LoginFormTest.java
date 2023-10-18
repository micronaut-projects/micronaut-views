package io.micronaut.views.fields.formsexamples;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.*;
import io.micronaut.views.fields.annotations.InputPassword;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static io.micronaut.views.fields.formsexamples.FormElementFixture.*;

@Property(name = "spec.name", value = "LoginFormTest")
@MicronautTest(startApplication = false)
class LoginFormTest {

    @Test
    void fieldsetGenerationForALoginForm(FieldsetGenerator fieldsetGenerator, LoginFormValidator validator) {
        Fieldset fieldset = fieldsetGenerator.generate(LoginForm.class);
        assertNotNull(fieldset);
        assertEquals(2, fieldset.getFields().size());
        assertTrue(fieldset.getFields().stream().anyMatch( it -> it instanceof InputTextFormElement));
        assertTrue(fieldset.getFields().stream().anyMatch( it -> it instanceof InputPasswordFormElement));
        InputTextFormElement usernameExpectation = usernameExpectation().build();
        assertTrue(assertFormElement(fieldset, usernameExpectation));
        InputPasswordFormElement inputPasswordFormElement = passwordExpectation().build();
        assertTrue(assertFormElement(fieldset, inputPasswordFormElement));

        LoginForm loginForm = new LoginForm("sherlock", "elementary");
        fieldset = fieldsetGenerator.generate(loginForm);
        assertNotNull(fieldset);
        assertEquals(2, fieldset.getFields().size());
        assertTrue(fieldset.getFields().stream().anyMatch( it -> it instanceof InputTextFormElement));
        assertTrue(fieldset.getFields().stream().anyMatch( it -> it instanceof InputPasswordFormElement));
        usernameExpectation = usernameExpectation().value("sherlock").build();
        assertTrue(assertFormElement(fieldset, usernameExpectation));
        inputPasswordFormElement = passwordExpectation().value("elementary").build();
        assertTrue(assertFormElement(fieldset, inputPasswordFormElement));

        LoginForm invalid = new LoginForm("sherlock", "");
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> validator.validate(invalid));
        fieldset = fieldsetGenerator.generate(invalid, ex);
        assertNotNull(fieldset);
        assertEquals(2, fieldset.getFields().size());
        assertTrue(fieldset.getFields().stream().anyMatch( it -> it instanceof InputTextFormElement));
        assertTrue(fieldset.getFields().stream().anyMatch( it -> it instanceof InputPasswordFormElement));
        usernameExpectation = usernameExpectation().value("sherlock").build();
        assertTrue(assertFormElement(fieldset, usernameExpectation));
        inputPasswordFormElement = passwordExpectation().value("").errors(Collections.singletonList(new SimpleMessage("must not be blank", "loginform.password.notblank"))).build();
        assertTrue(assertFormElement(fieldset, inputPasswordFormElement));
    }

    private InputTextFormElement.Builder usernameExpectation() {
        return InputTextFormElement.builder().required(true).id("username").name("username").label(new SimpleMessage("Username", "loginform.username"));
    }

    private InputPasswordFormElement.Builder passwordExpectation() {
        return InputPasswordFormElement.builder().required(true).id("password").name("password").label(new SimpleMessage("Password", "loginform.password"));
    }

    @Property(name = "spec.name", value = "LoginFormTest")
    @Singleton
    static class LoginFormValidator {
        void validate(@Valid LoginForm loginForm) {
        }
    }

    @Introspected
    record LoginForm(@NonNull @NotBlank String username, @InputPassword @NonNull @NotBlank String password) {
    }

}

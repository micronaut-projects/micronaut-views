package io.micronaut.views.fields.formsexamples;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.Fieldset;
import io.micronaut.views.fields.FieldsetGenerator;
import io.micronaut.views.fields.elements.InputPasswordFormElement;
import io.micronaut.views.fields.elements.InputTextFormElement;
import io.micronaut.views.fields.message.SimpleMessage;
import io.micronaut.views.fields.annotations.InputPassword;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static io.micronaut.views.fields.TestUtils.assertAnyInstance;
import static io.micronaut.views.fields.TestUtils.assertAnyMatch;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Property(name = "spec.name", value = "LoginFormTest")
@MicronautTest(startApplication = false)
class LoginFormTest {

    @Test
    void fieldsetGenerationForALoginForm(FieldsetGenerator fieldsetGenerator, LoginFormValidator validator) {
        Fieldset fieldset = fieldsetGenerator.generate(LoginForm.class);
        assertNotNull(fieldset);
        assertEquals(2, fieldset.fields().size());
        assertAnyInstance(fieldset.fields(), InputTextFormElement.class);
        assertAnyInstance(fieldset.fields(), InputPasswordFormElement.class);
        InputTextFormElement usernameExpectation = usernameExpectation().build();
        assertAnyMatch(fieldset, usernameExpectation);
        InputPasswordFormElement inputPasswordFormElement = passwordExpectation().build();
        assertAnyMatch(fieldset, inputPasswordFormElement);

        LoginForm loginForm = new LoginForm("sherlock", "elementary");
        fieldset = fieldsetGenerator.generate(loginForm);
        assertNotNull(fieldset);
        assertEquals(2, fieldset.fields().size());
        assertAnyInstance(fieldset.fields(), InputTextFormElement.class);
        assertAnyInstance(fieldset.fields(), InputPasswordFormElement.class);
        usernameExpectation = usernameExpectation().value("sherlock").build();
        assertAnyMatch(fieldset, usernameExpectation);
        inputPasswordFormElement = passwordExpectation().value("elementary").build();
        assertAnyMatch(fieldset, inputPasswordFormElement);

        LoginForm invalid = new LoginForm("sherlock", "");
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> validator.validate(invalid));
        fieldset = fieldsetGenerator.generate(invalid, ex);
        assertNotNull(fieldset);
        assertEquals(2, fieldset.fields().size());
        assertAnyInstance(fieldset.fields(), InputTextFormElement.class);
        assertAnyInstance(fieldset.fields(), InputPasswordFormElement.class);
        usernameExpectation = usernameExpectation().value("sherlock").build();
        assertAnyMatch(fieldset, usernameExpectation);
        inputPasswordFormElement = passwordExpectation().value("").errors(Collections.singletonList(new SimpleMessage("must not be blank", "loginform.password.notblank"))).build();
        assertAnyMatch(fieldset, inputPasswordFormElement);
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

package io.micronaut.views.fields.formsexamples;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.*;
import io.micronaut.views.fields.annotations.InputEmail;
import io.micronaut.views.fields.annotations.InputPassword;
import io.micronaut.views.fields.annotations.InputTel;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static io.micronaut.views.fields.formsexamples.FormElementFixture.assertFormElement;
import static org.junit.jupiter.api.Assertions.*;

@Property(name = "spec.name", value = "SignupFormTest")
@MicronautTest(startApplication = false)
class SignupFormTest {

    @Test
    void fieldsetGenerationForALoginForm(FieldsetGenerator fieldsetGenerator, SignupFormValidator validator) {
        Fieldset fieldset = fieldsetGenerator.generate(SignupForm.class);
        assertNotNull(fieldset);
        assertFalse(fieldset.hasErrors());
        assertNotNull(fieldset.getErrors());
        assertEquals(0, fieldset.getErrors().size());

        assertEquals(8, fieldset.getFields().size());

        assertTrue(fieldset.getFields().stream().anyMatch( it -> it instanceof InputCheckboxFormElement));

        InputTextFormElement firstNameExpectation = firstNameExpectation().build();
        assertTrue(assertFormElement(fieldset, firstNameExpectation));

        InputTextFormElement lastNameExpectation = lastNameExpectation().build();
        assertTrue(assertFormElement(fieldset, lastNameExpectation));

        InputTelFormElement phonexpectation = phonexpectation().build();
        assertTrue(assertFormElement(fieldset, phonexpectation));

        InputTextFormElement zipCodeExpectation = zipCodeExpectation().build();
        assertTrue(assertFormElement(fieldset, zipCodeExpectation));

        InputEmailFormElement emailExpectation = emailExpectation().build();
        assertTrue(assertFormElement(fieldset, emailExpectation));

        InputPasswordFormElement passwordExpectation = passwordExpectation().build();
        assertTrue(assertFormElement(fieldset, passwordExpectation));

        InputPasswordFormElement confirmPasswordExpectation = confirmPasswordExpectation().build();
        assertTrue(assertFormElement(fieldset, confirmPasswordExpectation));

        InputCheckboxFormElement acceptTermsExpectation = acceptTermsExpectation(checkboxBuilder -> checkboxBuilder.checked(false).value("false")).build();
        assertTrue(assertFormElement(fieldset, acceptTermsExpectation));

        SignupForm valid = new SignupForm("Sergio", "del Amo", null, null, "sergio.delamo@softamo.com", "elementary", "elementary", true);
        assertDoesNotThrow(() -> validator.validate(valid));
        assertNotNull(fieldset);
        assertFalse(fieldset.hasErrors());
        assertNotNull(fieldset.getErrors());
        assertEquals(0, fieldset.getErrors().size());

        fieldset = fieldsetGenerator.generate(valid);
        assertNotNull(fieldset);
        firstNameExpectation = firstNameExpectation().value("Sergio").build();
        assertTrue(assertFormElement(fieldset, firstNameExpectation));

        lastNameExpectation = lastNameExpectation().value("del Amo").build();
        assertTrue(assertFormElement(fieldset, lastNameExpectation));

        phonexpectation = phonexpectation().build();
        assertTrue(assertFormElement(fieldset, phonexpectation));

        zipCodeExpectation = zipCodeExpectation().build();
        assertTrue(assertFormElement(fieldset, zipCodeExpectation));

        emailExpectation = emailExpectation().value("sergio.delamo@softamo.com").build();
        assertTrue(assertFormElement(fieldset, emailExpectation));

        passwordExpectation = passwordExpectation().value("elementary").build();
        assertTrue(assertFormElement(fieldset, passwordExpectation));

        confirmPasswordExpectation = confirmPasswordExpectation().value("elementary").build();
        assertTrue(assertFormElement(fieldset, confirmPasswordExpectation));

        acceptTermsExpectation = acceptTermsExpectation(checkboxBuilder -> checkboxBuilder.checked(true).value("true")).build();
        assertTrue(assertFormElement(fieldset, acceptTermsExpectation));

        SignupForm dontAcceptTerms = new SignupForm("Sergio", "del Amo", null, null, "sergio.delamo@softamo.com", "elementary", "elementary", false);
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> validator.validate(dontAcceptTerms));
        fieldset = fieldsetGenerator.generate(dontAcceptTerms, ex);
        assertNotNull(fieldset);

        acceptTermsExpectation = acceptTermsExpectation(checkboxBuilder -> checkboxBuilder.checked(false).value("false").errors(Collections.singletonList(new SimpleMessage("must be true", "signupform.acceptterms.asserttrue")))).build();
        assertTrue(assertFormElement(fieldset, acceptTermsExpectation));

        SignupForm invalid  = new SignupForm("Sergio", "del Amo", null, null, "sergio.delamo@softamo.com", "elementary", "bar", true);
        ex = assertThrows(ConstraintViolationException.class, () -> validator.validate(invalid));
        fieldset = fieldsetGenerator.generate(invalid, ex);
        assertNotNull(fieldset);
        firstNameExpectation = firstNameExpectation().value("Sergio").build();
        assertTrue(assertFormElement(fieldset, firstNameExpectation));

        lastNameExpectation = lastNameExpectation().value("del Amo").build();
        assertTrue(assertFormElement(fieldset, lastNameExpectation));

        phonexpectation = phonexpectation().build();
        assertTrue(assertFormElement(fieldset, phonexpectation));

        zipCodeExpectation = zipCodeExpectation().build();
        assertTrue(assertFormElement(fieldset, zipCodeExpectation));

        emailExpectation = emailExpectation().value("sergio.delamo@softamo.com").build();
        assertTrue(assertFormElement(fieldset, emailExpectation));

        passwordExpectation = passwordExpectation().value("elementary").build();
        assertTrue(assertFormElement(fieldset, passwordExpectation));

        confirmPasswordExpectation = confirmPasswordExpectation().value("bar").build();
        assertTrue(assertFormElement(fieldset, confirmPasswordExpectation));

        assertTrue(fieldset.hasErrors());
        assertEquals(1, fieldset.getErrors().size());
        assertEquals(new SimpleMessage( "Passwords do not match", "signupform.passwordmatch"), fieldset.getErrors().get(0));

        acceptTermsExpectation = acceptTermsExpectation(checkboxBuilder -> checkboxBuilder.checked(true).value("true")).build();
        assertTrue(assertFormElement(fieldset, acceptTermsExpectation));
    }

    @PasswordMatch
    @Introspected
    record SignupForm(@NotBlank String firstName,
                      @NotBlank String lastName,
                      @InputTel @Nullable String phone,
                      @Nullable String zipCode,
                      @InputEmail @NotBlank String email,
                      @InputPassword @NotBlank String password,
                      @InputPassword @NotBlank String confirmPassword,
                      @AssertTrue boolean acceptTerms) {
    }

    @Property(name = "spec.name", value = "SignupFormTest")
    @Singleton
    static class SignupFormValidator {
        void validate(@Valid SignupForm signupForm) {
        }
    }

    private InputCheckboxFormElement.Builder acceptTermsExpectation(Consumer<Checkbox.Builder> builderConsumer) {
        Checkbox.Builder builder = Checkbox.builder().id("acceptTerms").name("acceptTerms").label(new SimpleMessage("Accept Terms", "signupform.acceptTerms"));
        if (builderConsumer != null) {
            builderConsumer.accept(builder);
        }
        List<Checkbox> checkboxList = Collections.singletonList(builder.build());
        return InputCheckboxFormElement.builder().checkboxes(checkboxList);
    }

    private InputTextFormElement.Builder firstNameExpectation() {
        return InputTextFormElement.builder().required(true).id("firstName").name("firstName").label(new SimpleMessage( "First Name", "signupform.firstName"));
    }

    private InputTextFormElement.Builder lastNameExpectation() {
        return InputTextFormElement.builder().required(true).id("lastName").name("lastName").label(new SimpleMessage("Last Name", "signupform.lastName"));
    }

    private InputTelFormElement.Builder phonexpectation() {
        return InputTelFormElement.builder().required(false).id("phone").name("phone").label(new SimpleMessage("Phone", "signupform.phone"));
    }

    private InputTextFormElement.Builder zipCodeExpectation() {
        return InputTextFormElement.builder().required(false).id("zipCode").name("zipCode").label(new SimpleMessage("Zip Code", "signupform.zipCode"));
    }

    private InputEmailFormElement.Builder emailExpectation() {
        return InputEmailFormElement.builder().required(true).id("email").name("email").label(new SimpleMessage("Email", "signupform.email"));
    }

    private InputPasswordFormElement.Builder passwordExpectation() {
        return InputPasswordFormElement.builder().required(true).id("password").name("password").label(new SimpleMessage("Password", "signupform.password"));
    }

    private InputPasswordFormElement.Builder confirmPasswordExpectation() {
        return InputPasswordFormElement.builder().required(true).id("confirmPassword").name("confirmPassword").label(new SimpleMessage("Confirm Password", "signupform.confirmPassword"));
    }

}

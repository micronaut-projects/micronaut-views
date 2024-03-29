package io.micronaut.views.fields.formsexamples;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.StringUtils;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.Fieldset;
import io.micronaut.views.fields.FieldsetGenerator;
import io.micronaut.views.fields.annotations.InputEmail;
import io.micronaut.views.fields.annotations.InputPassword;
import io.micronaut.views.fields.annotations.InputTel;
import io.micronaut.views.fields.elements.*;
import io.micronaut.views.fields.messages.Message;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static io.micronaut.views.fields.TestUtils.assertAnyInstance;
import static io.micronaut.views.fields.TestUtils.assertAnyMatch;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "spec.name", value = "SignupFormTest")
@MicronautTest(startApplication = false)
class SignupFormTest {

    @Test
    void fieldsetGenerationForALoginForm(FieldsetGenerator fieldsetGenerator) {
        Fieldset fieldset = fieldsetGenerator.generate(SignupForm.class);

        assertNotNull(fieldset);
        assertFalse(fieldset.hasErrors());
        assertNotNull(fieldset.errors());
        assertEquals(0, fieldset.errors().size());
        assertEquals(8, fieldset.fields().size());
    }

    @Test
    void blankLoginFormHasExpectedFields(FieldsetGenerator fieldsetGenerator) {
        Fieldset fieldset = fieldsetGenerator.generate(SignupForm.class);

        assertAnyInstance(fieldset.fields(), InputCheckboxFormElement.class);

        InputTextFormElement firstNameExpectation = firstNameExpectation().build();
        assertAnyMatch(fieldset, firstNameExpectation);

        InputTextFormElement lastNameExpectation = lastNameExpectation().build();
        assertAnyMatch(fieldset, lastNameExpectation);

        InputTelFormElement phonexpectation = phonexpectation().build();
        assertAnyMatch(fieldset, phonexpectation);

        InputTextFormElement zipCodeExpectation = zipCodeExpectation().build();
        assertAnyMatch(fieldset, zipCodeExpectation);

        InputEmailFormElement emailExpectation = emailExpectation().build();
        assertAnyMatch(fieldset, emailExpectation);

        InputPasswordFormElement passwordExpectation = passwordExpectation().build();
        assertAnyMatch(fieldset, passwordExpectation);

        InputPasswordFormElement confirmPasswordExpectation = confirmPasswordExpectation().build();
        assertAnyMatch(fieldset, confirmPasswordExpectation);

        InputCheckboxFormElement acceptTermsExpectation = acceptTermsExpectation(checkboxBuilder -> checkboxBuilder.checked(false).value("false")).build();
        assertAnyMatch(fieldset, acceptTermsExpectation);
    }

    @Test
    void loginFormValidationHappyPath(FieldsetGenerator fieldsetGenerator, SignupFormValidator validator) {
        SignupForm valid = new SignupForm("Sergio", "del Amo", null, null, "sergio.delamo@softamo.com", "elementary", "elementary", true);
        assertDoesNotThrow(() -> validator.validate(valid));

        Fieldset fieldset = fieldsetGenerator.generate(valid);
        assertNotNull(fieldset);

        InputTextFormElement firstNameExpectation = firstNameExpectation().value("Sergio").build();
        assertAnyMatch(fieldset, firstNameExpectation);

        InputTextFormElement lastNameExpectation = lastNameExpectation().value("del Amo").build();
        assertAnyMatch(fieldset, lastNameExpectation);

        InputTelFormElement phonexpectation = phonexpectation().build();
        assertAnyMatch(fieldset, phonexpectation);

        InputTextFormElement zipCodeExpectation = zipCodeExpectation().build();
        assertAnyMatch(fieldset, zipCodeExpectation);

        InputEmailFormElement emailExpectation = emailExpectation().value("sergio.delamo@softamo.com").build();
        assertAnyMatch(fieldset, emailExpectation);

        InputPasswordFormElement passwordExpectation = passwordExpectation().value("elementary").build();
        assertAnyMatch(fieldset, passwordExpectation);

        InputPasswordFormElement confirmPasswordExpectation = confirmPasswordExpectation().value("elementary").build();
        assertAnyMatch(fieldset, confirmPasswordExpectation);

        InputCheckboxFormElement acceptTermsExpectation = acceptTermsExpectation(checkboxBuilder -> checkboxBuilder.checked(true).value("true")).build();
        assertAnyMatch(fieldset, acceptTermsExpectation);
    }

    @Test
    void testSignupFormWithoutTerms(FieldsetGenerator fieldsetGenerator, SignupFormValidator validator) {
        SignupForm dontAcceptTerms = new SignupForm(
            "Sergio",
            "del Amo",
            null,
            null,
            "sergio.delamo@softamo.com",
            "elementary",
            "elementary",
            false // does not accept terms
        );
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> validator.validate(dontAcceptTerms));

        Fieldset fieldset = fieldsetGenerator.generate(dontAcceptTerms, ex);
        assertNotNull(fieldset);

        InputCheckboxFormElement acceptTermsExpectation = acceptTermsExpectationMustBeTrue().build();
        assertAnyMatch(fieldset, acceptTermsExpectation);
    }

    @Test
    void testSignupWithPasswordMismatch(FieldsetGenerator fieldsetGenerator, SignupFormValidator validator) {
        SignupForm invalid = new SignupForm(
            "Sergio",
            "del Amo",
            null,
            null,
            "sergio.delamo@softamo.com",
            "elementary",
            "bar", // password mismatch
            true
        );

        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> validator.validate(invalid));
        Fieldset fieldset = fieldsetGenerator.generate(invalid, ex);
        assertNotNull(fieldset);

        InputTextFormElement firstNameExpectation = firstNameExpectation().value("Sergio").build();
        assertAnyMatch(fieldset, firstNameExpectation);

        InputTextFormElement lastNameExpectation = lastNameExpectation().value("del Amo").build();
        assertAnyMatch(fieldset, lastNameExpectation);

        InputTelFormElement phonexpectation = phonexpectation().build();
        assertAnyMatch(fieldset, phonexpectation);

        InputTextFormElement zipCodeExpectation = zipCodeExpectation().build();
        assertAnyMatch(fieldset, zipCodeExpectation);

        InputEmailFormElement emailExpectation = emailExpectation().value("sergio.delamo@softamo.com").build();
        assertAnyMatch(fieldset, emailExpectation);

        InputPasswordFormElement passwordExpectation = passwordExpectation().value("elementary").build();
        assertAnyMatch(fieldset, passwordExpectation);

        InputPasswordFormElement confirmPasswordExpectation = confirmPasswordExpectation().value("bar").build();
        assertAnyMatch(fieldset, confirmPasswordExpectation);

        assertTrue(fieldset.hasErrors());
        assertEquals(1, fieldset.errors().size());
        assertEquals(Message.of("Passwords do not match", "signupform.passwordmatch"), fieldset.errors().get(0));

        InputCheckboxFormElement acceptTermsExpectation = acceptTermsExpectation(checkboxBuilder -> checkboxBuilder.checked(true).value("true")).build();
        assertAnyMatch(fieldset, acceptTermsExpectation);
    }

    private InputCheckboxFormElement.Builder acceptTermsExpectation(Consumer<Checkbox.Builder> builderConsumer) {
        Checkbox.Builder builder = Checkbox.builder().id("acceptTerms").name("acceptTerms").label(Message.of("Accept Terms", "signupform.acceptTerms"));
        if (builderConsumer != null) {
            builderConsumer.accept(builder);
        }
        List<Checkbox> checkboxList = Collections.singletonList(builder.build());
        return InputCheckboxFormElement.builder().label(Message.of("Accept Terms", "signupform.acceptTerms")).checkboxes(checkboxList);
    }

    private InputCheckboxFormElement.Builder acceptTermsExpectationMustBeTrue() {
        Checkbox.Builder builder = Checkbox.builder().id("acceptTerms").name("acceptTerms")
            .value(StringUtils.FALSE)
            .label(Message.of("Accept Terms", "signupform.acceptTerms"));
        List<Checkbox> checkboxList = Collections.singletonList(builder.build());
        return InputCheckboxFormElement.builder()
            .errors(Collections.singletonList(Message.of("must be true", "signupform.acceptterms.asserttrue")))
            .label(Message.of("Accept Terms", "signupform.acceptTerms")).checkboxes(checkboxList);
    }

    private InputTextFormElement.Builder firstNameExpectation() {
        return InputTextFormElement.builder().required(true).id("firstName").name("firstName").label(Message.of("First Name", "signupform.firstName"));
    }

    private InputTextFormElement.Builder lastNameExpectation() {
        return InputTextFormElement.builder().required(true).id("lastName").name("lastName").label(Message.of("Last Name", "signupform.lastName"));
    }

    private InputTelFormElement.Builder phonexpectation() {
        return InputTelFormElement.builder().required(false).id("phone").name("phone").label(Message.of("Phone", "signupform.phone"));
    }

    private InputTextFormElement.Builder zipCodeExpectation() {
        return InputTextFormElement.builder().required(false).id("zipCode").name("zipCode").label(Message.of("Zip Code", "signupform.zipCode"));
    }

    private InputEmailFormElement.Builder emailExpectation() {
        return InputEmailFormElement.builder().required(true).id("email").name("email").label(Message.of("Email", "signupform.email"));
    }

    private InputPasswordFormElement.Builder passwordExpectation() {
        return InputPasswordFormElement.builder().required(true).id("password").name("password").label(Message.of("Password", "signupform.password"));
    }

    private InputPasswordFormElement.Builder confirmPasswordExpectation() {
        return InputPasswordFormElement.builder().required(true).id("confirmPassword").name("confirmPassword").label(Message.of("Confirm Password", "signupform.confirmPassword"));
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
}

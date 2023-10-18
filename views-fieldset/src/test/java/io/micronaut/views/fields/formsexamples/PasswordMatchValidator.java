package io.micronaut.views.fields.formsexamples;

import io.micronaut.core.annotation.Introspected;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Introspected
class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, SignupFormTest.SignupForm> {
    @Override
    public boolean isValid(SignupFormTest.SignupForm value, ConstraintValidatorContext context) {
        if (value.password() == null && value.confirmPassword() == null) {
            return true;
        }
        if (value.password() != null && value.confirmPassword() == null) {
            return false;
        }
        if (value.password() == null) {
            return false;
        }
        return value.password().equals(value.confirmPassword());
    }
}

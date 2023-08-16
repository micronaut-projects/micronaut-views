package io.micronaut.views.fields.classlevelvalidation;

import io.micronaut.core.annotation.Introspected;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Introspected
class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, User> {
    @Override
    public boolean isValid(User value, ConstraintValidatorContext context) {
        if (value.password() == null && value.repeatPassword() == null) {
            return true;
        }
        if (value.password() != null && value.repeatPassword() == null) {
            return false;
        }
        if (value.password() == null) {
            return false;
        }
        return value.password().equals(value.repeatPassword());
    }
}

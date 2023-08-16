package io.micronaut.views.fields.tests.signup

import io.micronaut.core.annotation.Introspected
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

@Introspected
class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, User> {
    @Override
    boolean isValid(User value, ConstraintValidatorContext context) {
        if (value == null && value.repeatPassword == null) {
            return true
        }
        if (value != null && value.repeatPassword == null) {
            return false
        }
        if (value == null && value.repeatPassword != null) {
            return false
        }
        value.password == value.repeatPassword
    }
}

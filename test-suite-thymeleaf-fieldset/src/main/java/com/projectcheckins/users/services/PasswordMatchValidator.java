package com.projectcheckins.users.services;

import io.micronaut.core.annotation.Introspected;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Introspected
class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UserSave> {
    @Override
    public boolean isValid(UserSave value, ConstraintValidatorContext context) {
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


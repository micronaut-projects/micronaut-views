package com.projectcheckins.users.services;

import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.views.fields.annotations.InputEmail;
import io.micronaut.views.fields.annotations.InputPassword;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record UserSave(
    @InputEmail
    @NotBlank String email,
    @InputPassword
    @NotBlank String password,
    @InputPassword @NotBlank String confirmPassword) {
}

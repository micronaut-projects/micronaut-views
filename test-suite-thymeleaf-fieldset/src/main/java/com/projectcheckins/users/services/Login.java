package com.projectcheckins.users.services;

import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.views.fields.annotations.InputEmail;
import io.micronaut.views.fields.annotations.InputPassword;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record Login(
        @InputEmail
        @NotBlank String username,
        @InputPassword
        @NotBlank String password) {
}

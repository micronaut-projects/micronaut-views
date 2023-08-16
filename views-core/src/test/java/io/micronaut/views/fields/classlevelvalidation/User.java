package io.micronaut.views.fields.classlevelvalidation;

import io.micronaut.core.annotation.Introspected;
import jakarta.validation.constraints.NotBlank;

@PasswordMatch
@Introspected
public record User(
    @NotBlank
    String username,

    @NotBlank
    String password,

    @NotBlank
    String repeatPassword
) {
}

package io.micronaut.views.fields.tests.signup

import io.micronaut.core.annotation.Introspected
import jakarta.validation.constraints.NotBlank

@PasswordMatch
@Introspected
class User {

    @NotBlank
    String username

    @NotBlank
    String password

    @NotBlank
    String repeatPassword
}

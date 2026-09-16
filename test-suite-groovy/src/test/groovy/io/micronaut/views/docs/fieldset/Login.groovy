package io.micronaut.views.docs.fieldset

import io.micronaut.core.annotation.Introspected
import io.micronaut.views.fields.annotations.InputPassword
import jakarta.validation.constraints.NotBlank

@Introspected
class Login {
    @NotBlank
    final String username

    @InputPassword
    @NotBlank
    final String password

    Login(String username, String password) {
        this.username = username
        this.password = password
    }
}

package io.micronaut.views.fields.forms

import groovy.transform.CompileStatic
import io.micronaut.core.annotation.Introspected
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@CompileStatic
@Introspected
class Contact {
    @NotBlank String firstName
    @NotBlank String lastName
    @NotBlank @Email String email
}

package io.micronaut.views.docs.fieldset

import io.micronaut.core.annotation.Introspected
import io.micronaut.views.fields.annotations.InputPassword
import jakarta.validation.constraints.NotBlank

@Introspected
data class Login(@field:NotBlank val username: String,
                 @field:InputPassword @field:NotBlank val password: String)

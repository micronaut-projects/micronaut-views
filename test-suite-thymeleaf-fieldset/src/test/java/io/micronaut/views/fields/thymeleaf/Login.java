package io.micronaut.views.fields.thymeleaf;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.views.fields.annotations.InputPassword;
import jakarta.validation.constraints.NotBlank;

@Introspected
public record Login(@NotBlank String username,
                    @InputPassword @NotBlank String password) {
}

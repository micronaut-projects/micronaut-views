package com.projectcheckins.services;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import jakarta.validation.constraints.NotBlank;

@Introspected
public record AnswerRow(@NonNull @NotBlank String answer) {
}

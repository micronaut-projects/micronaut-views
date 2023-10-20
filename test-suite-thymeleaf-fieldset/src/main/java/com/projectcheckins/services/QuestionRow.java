package com.projectcheckins.services;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

@Introspected
public record QuestionRow(@NonNull @NotNull Long id,
                          @NonNull @NotBlank String title,
                          @NonNull @NotNull LocalTime localTime,
                          long numberOfUsersBeingAsked) {
}

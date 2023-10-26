package com.projectcheckins.services;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.views.fields.annotations.InputHidden;
import io.micronaut.views.fields.annotations.TrixEditor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Serdeable
public record AnswerSave(@InputHidden @NonNull @NotNull Long questionId,
                         @TrixEditor @NonNull @NotBlank String answer) {
}

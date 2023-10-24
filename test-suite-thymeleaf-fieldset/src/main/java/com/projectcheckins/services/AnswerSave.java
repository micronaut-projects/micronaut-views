package com.projectcheckins.services;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.views.fields.annotations.InputHidden;
import jakarta.validation.constraints.NotBlank;

@Introspected
public record AnswerSave(@InputHidden Long questionId,
                         @NotBlank String answer) {
}

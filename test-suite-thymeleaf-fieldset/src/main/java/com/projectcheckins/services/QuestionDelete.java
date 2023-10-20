package com.projectcheckins.services;

import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.views.fields.annotations.InputHidden;

@Serdeable
public record QuestionDelete(@InputHidden Long questionId) {
}

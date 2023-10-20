package com.projectcheckins.repositories;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.Embeddable;

@Introspected
@Embeddable
public record QuestionUserId(Long questionId, Long userId) {
}

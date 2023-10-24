package com.projectcheckins.repositories.jdbc;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.Embeddable;

@Introspected
@Embeddable
public record QuestionUserId(Long questionId, Long userId) {
}

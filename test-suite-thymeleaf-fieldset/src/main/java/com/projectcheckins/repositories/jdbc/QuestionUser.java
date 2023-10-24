package com.projectcheckins.repositories.jdbc;

import io.micronaut.data.annotation.EmbeddedId;
import io.micronaut.data.annotation.MappedEntity;
@MappedEntity
public record QuestionUser(@EmbeddedId QuestionUserId id) {
}

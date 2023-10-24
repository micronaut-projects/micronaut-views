package com.projectcheckins.repositories.jdbc;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

@JdbcRepository(dialect = Dialect.H2)
public interface QuestionRepository extends CrudRepository<Question, Long> {
    @Join(value = "answers")
    Optional<Question> getById(@NonNull Long id);
}

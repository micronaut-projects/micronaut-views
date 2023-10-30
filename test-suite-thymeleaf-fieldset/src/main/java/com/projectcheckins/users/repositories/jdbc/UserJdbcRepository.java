package com.projectcheckins.users.repositories.jdbc;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotBlank;

import java.util.Optional;

@JdbcRepository(dialect = Dialect.H2)
public interface UserJdbcRepository extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(@NonNull @NotBlank String username);

    Optional<UserEntity> findByEmail(@NonNull @NotBlank String email);
}

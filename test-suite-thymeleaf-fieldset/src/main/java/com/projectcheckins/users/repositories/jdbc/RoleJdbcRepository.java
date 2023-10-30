package com.projectcheckins.users.repositories.jdbc;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.H2)
public interface RoleJdbcRepository extends CrudRepository<RoleEntity, Long> {
    RoleEntity save(String authority);
    Optional<RoleEntity> findByAuthority(String authority);
}

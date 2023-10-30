package com.projectcheckins.users.repositories.jdbc;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;

@JdbcRepository(dialect = Dialect.H2)
public interface UserRoleJdbcRepository extends CrudRepository<UserRoleEntity, UserRoleId> {
    @Query("SELECT role_.`authority` FROM `role` role_ inner join `user_role` user_role_ ON user_role_.`user_role_id_role_id` = role_.`id` inner join `person` user_ ON user_role_.`user_role_id_user_id` = user_.`id` where user_.`username` = :username")
    List<String> findAllAuthoritiesByUsername(String username);
}

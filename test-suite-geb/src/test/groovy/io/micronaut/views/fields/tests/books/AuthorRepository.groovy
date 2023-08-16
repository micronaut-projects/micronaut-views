package io.micronaut.views.fields.tests.books

import io.micronaut.context.annotation.Requires
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.PageableRepository

@Requires(property = "spec.name", value = "AuthorCreateSpec")
@JdbcRepository(dialect = Dialect.H2)
interface AuthorRepository extends PageableRepository<AuthorEntity, Long> {
}

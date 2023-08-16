package io.micronaut.views.fields.tests.location

import io.micronaut.context.annotation.Requires
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository

@Requires(property = "spec.name", value = "LocationCreateSpec")
@JdbcRepository(dialect = Dialect.H2)
public interface LocationRepository extends CrudRepository<Location, Long> {
}

package io.micronaut.views.fields.tests.books

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import jakarta.validation.constraints.NotBlank

@MappedEntity("author")
class AuthorEntity {
    @Id
    @GeneratedValue
    Long id

    @NotBlank
    String name
}

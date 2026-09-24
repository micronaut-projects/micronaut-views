package io.micronaut.views.docs.fieldset

import jakarta.inject.Singleton

@Singleton
class AuthorRepository {

    fun findAll(): List<Author> = listOf(
        Author(1L, "Kishori Sharan"),
        Author(2L, "Peter Späth"),
        Author(3L, "Sam Newman"))
}

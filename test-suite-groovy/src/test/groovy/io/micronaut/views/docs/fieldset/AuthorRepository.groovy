package io.micronaut.views.docs.fieldset

import jakarta.inject.Singleton

@Singleton
class AuthorRepository {

    List<Author> findAll() {
        [
            new Author(1L, "Kishori Sharan"),
            new Author(2L, "Peter Späth"),
            new Author(3L, "Sam Newman")
        ]
    }
}

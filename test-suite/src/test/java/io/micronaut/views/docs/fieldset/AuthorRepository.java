package io.micronaut.views.docs.fieldset;

import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class AuthorRepository {

    public List<Author> findAll() {
        return List.of(
            new Author(1L, "Kishori Sharan"),
            new Author(2L, "Peter Späth"),
            new Author(3L, "Sam Newman"));
    }
}

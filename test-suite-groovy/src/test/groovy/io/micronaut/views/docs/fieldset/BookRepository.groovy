package io.micronaut.views.docs.fieldset

import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton

@Requires(property = "spec.name", value = "BookControllerTest")
@Singleton
class BookRepository {
    private final List<Book> books = Collections.synchronizedList(new ArrayList<>())

    List<Book> findAll() {
        new ArrayList<>(books)
    }

    Book save(Book book) {
        books.add(book)
        book
    }
}

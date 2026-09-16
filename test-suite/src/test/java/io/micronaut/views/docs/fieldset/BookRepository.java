package io.micronaut.views.docs.fieldset;

import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Requires(property = "spec.name", value = "BookControllerTest")
@Singleton
public class BookRepository {
    private final List<Book> books = Collections.synchronizedList(new ArrayList<>());

    public List<Book> findAll() {
        return new ArrayList<>(books);
    }

    public Book save(Book book) {
        books.add(book);
        return book;
    }
}

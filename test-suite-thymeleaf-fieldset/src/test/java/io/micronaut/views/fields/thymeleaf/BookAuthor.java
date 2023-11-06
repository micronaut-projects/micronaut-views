package io.micronaut.views.fields.thymeleaf;

import io.micronaut.data.annotation.EmbeddedId;
import io.micronaut.data.annotation.MappedEntity;

@MappedEntity
public class BookAuthor {
    @EmbeddedId
    private final BookAuthorId bookAuthorId;

    public BookAuthor(BookAuthorId bookAuthorId) {
        this.bookAuthorId = bookAuthorId;
    }

    public BookAuthorId getBookAuthorId() {
        return bookAuthorId;
    }
}



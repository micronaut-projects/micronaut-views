package io.micronaut.views.fields.thymeleaf;

import io.micronaut.core.annotation.Creator;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.Embeddable;

@Introspected
@Embeddable
public class BookAuthorId {
    private final Long bookId;
    private final Long authorId;

    @Creator
    public BookAuthorId(Long bookId,
                      Long authorId) {
        this.bookId = bookId;
        this.authorId = authorId;
    }

    public Long getBookId() {
        return bookId;
    }

    public Long getAuthorId() {
        return authorId;
    }
}

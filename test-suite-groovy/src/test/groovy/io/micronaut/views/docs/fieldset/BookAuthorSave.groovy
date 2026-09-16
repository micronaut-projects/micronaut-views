package io.micronaut.views.docs.fieldset

import io.micronaut.serde.annotation.Serdeable
import io.micronaut.views.fields.annotations.InputHidden
import io.micronaut.views.fields.annotations.Select
import jakarta.validation.constraints.NotNull

//tag::clazz[]
@Serdeable
class BookAuthorSave {
    @NotNull
    @InputHidden
    final Long bookId

    @NotNull
    @Select(fetcher = AuthorFetcher)
    final Long authorId

    BookAuthorSave(Long bookId, Long authorId) {
        this.bookId = bookId
        this.authorId = authorId
    }
}
//end::clazz[]

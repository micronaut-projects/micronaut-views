package io.micronaut.views.docs.fieldset

import io.micronaut.serde.annotation.Serdeable
import io.micronaut.views.fields.annotations.InputHidden
import io.micronaut.views.fields.annotations.Select
import jakarta.validation.constraints.NotNull

//tag::clazz[]
@Serdeable
data class BookAuthorSave(@field:NotNull @field:InputHidden val bookId: Long,
                          @field:NotNull @field:Select(fetcher = AuthorFetcher::class) val authorId: Long?)
//end::clazz[]

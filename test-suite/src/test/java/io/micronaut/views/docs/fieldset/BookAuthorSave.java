package io.micronaut.views.docs.fieldset;

import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.views.fields.annotations.InputHidden;
import io.micronaut.views.fields.annotations.Select;
import jakarta.validation.constraints.NotNull;

//tag::clazz[]
@Serdeable
public record BookAuthorSave(@NotNull @InputHidden Long bookId,
                             @NotNull @Select(fetcher = AuthorFetcher.class) Long authorId) {
}
//end::clazz[]

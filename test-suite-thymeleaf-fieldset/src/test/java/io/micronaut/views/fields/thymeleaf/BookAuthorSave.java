package io.micronaut.views.fields.thymeleaf;

import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.views.fields.annotations.InputHidden;
import io.micronaut.views.fields.annotations.Select;
import jakarta.validation.constraints.NotNull;

@Serdeable
public record BookAuthorSave(@NotNull @InputHidden Long bookId,
                             @NotNull @Select(fetcher = AuthorFetcher.class) Long authorId) {
}

package io.micronaut.views.docs.fieldset

import io.micronaut.serde.annotation.Serdeable
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Serdeable
class BookSave {
    @Size(min = 2, max = 255)
    @NotBlank
    final String title

    @Min(1L)
    @Max(21450L)
    @NotNull
    final Integer pages

    BookSave(String title, Integer pages) {
        this.title = title
        this.pages = pages
    }
}

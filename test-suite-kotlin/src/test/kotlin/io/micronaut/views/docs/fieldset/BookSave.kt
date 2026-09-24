package io.micronaut.views.docs.fieldset

import io.micronaut.serde.annotation.Serdeable
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Serdeable
data class BookSave(@field:Size(min = 2, max = 255) @field:NotBlank val title: String,
                    @field:Min(1) @field:Max(21450) @field:NotNull val pages: Int?)

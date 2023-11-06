package io.micronaut.views.fields.thymeleaf;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Serdeable
public record BookSave(@Size(min = 2, max = 255) @NotBlank String title,
                       @Min(1) @Max(21450) @NotNull Integer pages) {
}

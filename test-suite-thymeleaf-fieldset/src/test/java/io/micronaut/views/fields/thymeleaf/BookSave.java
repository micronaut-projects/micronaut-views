package io.micronaut.views.fields.thymeleaf;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Serdeable
public record BookSave(@NotBlank String title,
                       @Min(1) @NotNull Integer pages) {
}

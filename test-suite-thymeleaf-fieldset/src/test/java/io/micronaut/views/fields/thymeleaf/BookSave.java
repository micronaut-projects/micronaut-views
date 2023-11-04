package io.micronaut.views.fields.thymeleaf;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.*;

@Serdeable
public record BookSave(@Size(min = 2, max = 255) @NotBlank String title,
                       @Min(1) @Max(21450) @NotNull Integer pages) {
}

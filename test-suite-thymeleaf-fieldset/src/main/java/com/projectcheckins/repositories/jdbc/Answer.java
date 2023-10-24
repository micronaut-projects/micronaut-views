package com.projectcheckins.repositories.jdbc;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@MappedEntity
public record Answer(@Id @GeneratedValue @Nullable Long id,
                     @NonNull @NotBlank String answer,
                     @DateCreated LocalDateTime dateCreated,
                     @DateUpdated LocalDateTime dateUpdated,
                     @Relation(value = Relation.Kind.MANY_TO_ONE)
                     @Nullable Question question) {
}

package io.micronaut.views.fields.thymeleaf;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

@MappedEntity
public record Book(@Nullable @Id @GeneratedValue(GeneratedValue.Type.AUTO) Long id,
                   @NonNull String title,
                   @NonNull Integer pages) {
}

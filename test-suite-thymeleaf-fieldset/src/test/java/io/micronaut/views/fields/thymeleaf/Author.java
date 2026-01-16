package io.micronaut.views.fields.thymeleaf;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@MappedEntity
public record Author(@Nullable @Id @GeneratedValue(GeneratedValue.Type.AUTO) Long id,
                   @NonNull String title) {
}

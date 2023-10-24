package com.projectcheckins.repositories.jdbc;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

@MappedEntity
public record Question(@Id @GeneratedValue @Nullable Long id,
                       @NonNull @NotBlank String title,
                       @Nullable DayOfWeek onceAWeekOn,
                       @NotNull LocalTime timeOfDay,
                       @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "question")
                       Set<Answer> answers) {
}

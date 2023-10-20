package com.projectcheckins.repositories;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

@MappedEntity
public record Question(@Id @GeneratedValue @Nullable Long id,
                       @NonNull @NotBlank String title,
                       @Nullable DayOfWeek onceAWeekOn,
                       @NotNull LocalTime timeOfDay

                       ) {
}

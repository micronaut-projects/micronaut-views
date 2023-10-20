package com.projectcheckins.services;

import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.views.fields.annotations.InputCheckbox;
import io.micronaut.views.fields.annotations.InputRadio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Serdeable
public record QuestionSave(@NotBlank String question,
                           @NotNull DayOfWeek onceAWeekOn,
                           @NotNull LocalTime timeOfDay,
                           @NotEmpty @InputCheckbox(fetcher = UserCheckboxFetcher.class) List<Long> usersId) {
}

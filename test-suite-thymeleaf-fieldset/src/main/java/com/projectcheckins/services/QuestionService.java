package com.projectcheckins.services;

import io.micronaut.core.annotation.NonNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface QuestionService {
    @NonNull
    Long save(@NonNull @NotNull @Valid QuestionSave questionSave);

    @NonNull
    List<QuestionRow> findAll();

    Optional<QuestionRow> findById(@NonNull @NotNull Long questionId);

    void delete(@NonNull @NotNull @Valid QuestionDelete form);

    void update(@NonNull @NotNull @Valid QuestionUpdate questionUpdate);

    @NonNull Optional<QuestionUpdate> findUpdateForm(@NonNull @NotNull Long questionId);

    void saveAnswer(@NonNull @NotNull @Valid AnswerSave answerSave);

    @NonNull
    List<AnswerRow> findAnswersByQuestionId(@NonNull @NotNull Long questionId);
}

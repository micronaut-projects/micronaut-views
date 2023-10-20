package com.projectcheckins.repositories;

import com.projectcheckins.services.*;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Singleton
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionUserRepository questionUserRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository,
                               QuestionUserRepository questionUserRepository) {
        this.questionRepository = questionRepository;
        this.questionUserRepository = questionUserRepository;
    }

    @Transactional
    @Override
    public Long save(@NonNull @NotNull @Valid QuestionSave questionSave) {
        Question question = new Question(null, questionSave.question(), questionSave.onceAWeekOn(), questionSave.timeOfDay());
        question = questionRepository.save(question);
        for (Long userId : questionSave.usersId()) {
            questionUserRepository.save(new QuestionUser(new QuestionUserId(question.id(), userId)));
        }
        return question.id();
    }

    @Override
    public List<QuestionRow> findAll() {
        return questionRepository.findAll().stream()
            .map(this::toQuestionRow)
            .toList();
    }

    @Override
    public Optional<QuestionRow> findById(Long questionId) {
        return questionRepository.findById(questionId).map(this::toQuestionRow);
    }

    @Override
    public void delete(QuestionDelete form) {
        delete(form.questionId());
    }

    @Transactional
    @Override
    public void update(QuestionUpdate questionUpdate) {
        Long questionId = questionUpdate.questionId();
        Set<QuestionUserId> currentQuestionUserIds = new HashSet<>();
        for (Long userId : questionUserRepository.findAllUserIdByQuestionId(questionId)) {
            currentQuestionUserIds.add(new QuestionUserId(questionId, userId));
        }
        Set<QuestionUserId> newQuestionUserIds = new HashSet<>();
        for (Long userId : questionUpdate.usersId()) {
            newQuestionUserIds.add(new QuestionUserId(questionId, userId));
        }
        Set<QuestionUserId> deleteQuestionUserIds = new HashSet<>(currentQuestionUserIds);
        deleteQuestionUserIds.removeAll(newQuestionUserIds);
        for (QuestionUserId questionUserId : deleteQuestionUserIds) {
            questionUserRepository.delete(new QuestionUser(questionUserId));
        }
        newQuestionUserIds.removeAll(currentQuestionUserIds);
        for (QuestionUserId questionUserId : newQuestionUserIds) {
            questionUserRepository.save(new QuestionUser(questionUserId));
        }
        Question question = new Question(questionId,
            questionUpdate.question(),
            questionUpdate.onceAWeekOn(),
            questionUpdate.timeOfDay());
        questionRepository.update(question);
    }

    @Override
    public Optional<QuestionUpdate> findUpdateForm(Long questionId) {
        return questionRepository.findById(questionId)
            .map(question -> new QuestionUpdate(questionId,
                question.title(),
                question.onceAWeekOn(),
                question.timeOfDay(),
                questionUserRepository.findAllUserIdByQuestionId(questionId)));
    }

    @Transactional
    public void delete(Long questionId) {
        questionUserRepository.deleteByQuestionId(questionId);
        questionRepository.deleteById(questionId);
    }

    private QuestionRow toQuestionRow(Question question) {
        return new QuestionRow(question.id(),
            question.title(),
            question.timeOfDay(),
            questionUserRepository.countByQuestionId(question.id()));
    }
}

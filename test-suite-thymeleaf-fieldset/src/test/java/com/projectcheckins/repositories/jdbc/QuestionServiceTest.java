package com.projectcheckins.repositories.jdbc;

import com.projectcheckins.services.AnswerSave;
import com.projectcheckins.services.QuestionSave;
import com.projectcheckins.services.QuestionService;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false, transactional = false)
class QuestionServiceTest {

    @Test
    void saveQuestion(QuestionService questionService, UserRepository userRepository, QuestionRepository questionRepository) {
        Long sergioId = userRepository.save(new User(null, "Sergio del Amo")).id();
        Long timId = userRepository.save(new User(null, "Tim Yates")).id();
        QuestionSave questionSave = new QuestionSave("What are you working on this week?",
                DayOfWeek.MONDAY,
                LocalTime.of(16, 30),
                List.of(sergioId, timId));
        long old = questionRepository.count();
        Long questionId = questionService.save(questionSave);
        assertEquals(old + 1, questionRepository.count());
        questionRepository.deleteById(questionId);
        questionRepository.deleteById(sergioId);
        questionRepository.deleteById(timId);
    }

    @Test
    void saveAnswer(QuestionService questionService,
                    UserRepository userRepository,
                    QuestionRepository questionRepository,
                    AnswerRepository answerRepository) {
        Long sergioId = userRepository.save(new User(null, "Sergio del Amo")).id();
        Long timId = userRepository.save(new User(null, "Tim Yates")).id();
        QuestionSave questionSave = new QuestionSave("What are you working on this week?",
                DayOfWeek.MONDAY,
                LocalTime.of(16, 30),
                List.of(sergioId, timId));
        long old = questionRepository.count();
        Long questionId = questionService.save(questionSave);
        assertEquals(old + 1, questionRepository.count());

        assertEquals(0, countAnswersByQuestionId(questionRepository, questionId));

        old = answerRepository.count();
        AnswerSave answerSave = new AnswerSave(questionId, "I worked on Micronaut Security");
        questionService.saveAnswer(answerSave);
        assertEquals(old + 1, answerRepository.count());



        assertEquals(1, countAnswersByQuestionId(questionRepository, questionId));

        questionRepository.deleteById(questionId);
        questionRepository.deleteById(sergioId);
        questionRepository.deleteById(timId);
    }

    long countAnswersByQuestionId(QuestionRepository questionRepository, Long questionId) {
        Optional<Question> questionOptional = questionRepository.getById(questionId);
        if (questionOptional.isEmpty()) {
            questionOptional = questionRepository.findById(questionId);
        }
        assertTrue(questionOptional.isPresent());
        Question question = questionOptional.get();
        assertNotNull(question.answers());
        return question.answers().size();
    }
}

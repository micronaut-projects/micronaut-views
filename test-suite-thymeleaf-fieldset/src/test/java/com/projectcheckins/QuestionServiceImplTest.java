package com.projectcheckins;

import com.projectcheckins.repositories.QuestionRepository;
import com.projectcheckins.repositories.QuestionUserRepository;
import com.projectcheckins.repositories.User;
import com.projectcheckins.repositories.UserRepository;
import com.projectcheckins.services.QuestionSave;
import com.projectcheckins.services.QuestionService;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@MicronautTest(startApplication = false, transactional = false)
class QuestionServiceImplTest {

    @Test
    void saveQuestionNew(QuestionService questionService,
                         QuestionRepository questionRepository,
                         QuestionUserRepository questionUserRepository,
                         UserRepository userRepository) {
        //given:
        Long sergioId = userRepository.save(new User(null, "Sergio del Amo")).id();
        Long timId = userRepository.save(new User(null, "Tim Yates")).id();
        Long wetteId = userRepository.save(new User(null, "Dean Wette")).id();
        Long jeremyId = userRepository.save(new User(null, "Jeremy Grelle")).id();

        long count = questionRepository.count();

        QuestionSave questionSave = new QuestionSave("What are you working on this week?",
            DayOfWeek.MONDAY,
            LocalTime.of(16, 30),
            List.of(sergioId, timId));

        //when:
        Long questionId = questionService.save(questionSave);

        //then:
        assertEquals(count + 1, questionRepository.count());
        assertEquals(2, questionUserRepository.findAllUserIdByQuestionId(questionId).size());

        //cleanup:
        questionUserRepository.deleteAll();
        questionRepository.deleteById(questionId);
        userRepository.deleteById(sergioId);
        userRepository.deleteById(timId);
        userRepository.deleteById(wetteId);
        userRepository.deleteById(jeremyId);
    }


}

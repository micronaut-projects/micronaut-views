package com.projectcheckins;

import com.projectcheckins.repositories.User;
import com.projectcheckins.repositories.UserRepository;
import com.projectcheckins.services.QuestionSave;
import com.projectcheckins.services.QuestionService;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import jakarta.inject.Singleton;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Requires(notEnv = Environment.TEST)
@Singleton
public class Bootstrap implements ApplicationEventListener<StartupEvent> {
    private final UserRepository userRepository;
    private final QuestionService questionService;

    public Bootstrap(UserRepository userRepository,
                     QuestionService questionService) {
        this.userRepository = userRepository;
        this.questionService = questionService;
    }

    @Override
    public void onApplicationEvent(StartupEvent event) {

        Long sergioId = userRepository.save(new User(null, "Sergio del Amo")).id();
        Long timId = userRepository.save(new User(null, "Tim Yates")).id();
        Long wetteId = userRepository.save(new User(null, "Dean Wette")).id();
        Long jeremyGrelle = userRepository.save(new User(null, "Jeremy Grelle")).id();
        QuestionSave questionSave = new QuestionSave("What are you working on this week?",
            DayOfWeek.MONDAY,
            LocalTime.of(16, 30),
            List.of(sergioId, timId));
        questionService.save(questionSave);
    }
}

package com.projectcheckins.services;

import com.projectcheckins.repositories.jdbc.User;
import com.projectcheckins.repositories.jdbc.UserRepository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.views.fields.Checkbox;
import io.micronaut.views.fields.CheckboxFetcher;
import io.micronaut.views.fields.Message;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class UserCheckboxFetcher implements CheckboxFetcher<List<Long>> {

    private final UserRepository userRepository;

    public UserCheckboxFetcher(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<Checkbox> generate(Class<List<Long>> type) {
        return userRepository.findAll().stream().map(user -> checkboxForUser(user, null)).toList();
    }

    @Override
    public List<Checkbox> generate(List<Long> ids) {
        return userRepository.findAll().stream().map(user -> checkboxForUser(user, ids)).toList();
    }

    @NonNull
    private static Checkbox checkboxForUser(@NonNull User user,
                                            @Nullable List<Long> userIds) {
        return Checkbox.builder()
            .checked(userIds != null && userIds.stream().anyMatch(userId -> user.id().equals(userId)))
            .value(String.valueOf(user.id()))
            .name("usersId")
            .id("usersId" + user.id())
            .label(Message.of(user.name(), null))
            .build();
    }
}

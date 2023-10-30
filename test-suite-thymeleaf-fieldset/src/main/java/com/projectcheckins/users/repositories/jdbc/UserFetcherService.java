package com.projectcheckins.users.repositories.jdbc;

import com.projectcheckins.users.services.UserFetcher;
import com.projectcheckins.users.services.UserState;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotBlank;

import java.util.Optional;

@Singleton
class UserFetcherService implements UserFetcher {

    private final UserJdbcRepository userJdbcRepository;

    UserFetcherService(UserJdbcRepository userJdbcRepository) { // <2>
        this.userJdbcRepository = userJdbcRepository;
    }

    @Override
    public Optional<UserState> findByUsername(@NotBlank @NonNull String username) {
        return userJdbcRepository.findByUsername(username).map(UserState.class::cast);
    }

    @Override
    public Optional<UserState> findByEmail(@NotBlank @NonNull String email) {
        return userJdbcRepository.findByEmail(email).map(UserState.class::cast);
    }
}

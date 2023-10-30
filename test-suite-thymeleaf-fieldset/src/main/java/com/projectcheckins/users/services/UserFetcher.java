package com.projectcheckins.users.services;

import io.micronaut.core.annotation.NonNull;
import jakarta.validation.constraints.NotBlank;

import java.util.Optional;

public interface UserFetcher {
    Optional<UserState> findByUsername(@NotBlank @NonNull String username);

    Optional<UserState> findByEmail(String email);
}

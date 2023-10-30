package com.projectcheckins.users.services;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Collections;
import java.util.List;

public interface UserService {

    default void save(@NonNull @NotNull @Valid UserSave userSave) {
        save(userSave, Collections.emptyList());
    }


    void save(@NonNull @NotNull @Valid UserSave userSave,
              @Nullable List<String> authorities);
}

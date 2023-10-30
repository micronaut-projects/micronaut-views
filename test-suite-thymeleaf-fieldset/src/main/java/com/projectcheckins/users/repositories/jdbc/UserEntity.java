package com.projectcheckins.users.repositories.jdbc;

import com.projectcheckins.users.services.UserState;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import io.micronaut.data.annotation.*;

@MappedEntity("person")
public record UserEntity(@Id @GeneratedValue Long id,
                         @NotBlank String email,
                         @NotBlank String username,
                         @NotBlank String password,
                         boolean enabled,
                         boolean accountExpired,
                         boolean accountLocked,
                         boolean passwordExpired) implements UserState {

    @Override
    public String getUsername() {
        return username();
    }

    @Override
    public String getPassword() {
        return password();
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean isAccountExpired() {
        return this.accountExpired;
    }

    @Override
    public boolean isAccountLocked() {
        return this.accountLocked;
    }

    @Override
    public boolean isPasswordExpired() {
        return this.passwordExpired;
    }
}

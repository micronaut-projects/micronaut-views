package com.projectcheckins.users.repositories.jdbc;

import io.micronaut.data.annotation.EmbeddedId;
import io.micronaut.data.annotation.MappedEntity;

@MappedEntity("user_role")
public class UserRoleEntity  {
    @EmbeddedId
    private final UserRoleId userRoleId;

    public UserRoleEntity(UserRoleId userRoleId) {
        this.userRoleId = userRoleId;
    }

    public UserRoleId getUserRoleId() {
        return userRoleId;
    }
}

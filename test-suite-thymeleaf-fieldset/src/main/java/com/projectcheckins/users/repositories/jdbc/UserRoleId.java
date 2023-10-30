package com.projectcheckins.users.repositories.jdbc;

import io.micronaut.core.annotation.Creator;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.Embeddable;

@Introspected
@Embeddable
public class UserRoleId {
    private final Long userId;
    private final Long roleId;

    @Creator
    public UserRoleId(Long userId,
               Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getRoleId() {
        return roleId;
    }
}

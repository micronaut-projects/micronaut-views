package com.projectcheckins.users.repositories.jdbc;

import com.projectcheckins.users.services.AuthoritiesFetcher;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
class AuthoritiesFetcherService implements AuthoritiesFetcher {

    private final UserRoleJdbcRepository userRoleGormService;

    AuthoritiesFetcherService(UserRoleJdbcRepository userRoleGormService) {
        this.userRoleGormService = userRoleGormService;
    }

    @Override
    public List<String> findAuthoritiesByUsername(String username) {
        return userRoleGormService.findAllAuthoritiesByUsername(username);
    }
}

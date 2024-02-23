package io.micronaut.views.model.security;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestAuthenticationProvider;
import jakarta.inject.Singleton;

import java.util.Collections;

@Singleton
public class MockAuthenticationProvider<B> implements HttpRequestAuthenticationProvider<B> {

    @Override
    public AuthenticationResponse authenticate(@Nullable HttpRequest<B> httpRequest, AuthenticationRequest<String, String> authenticationRequest) {
        return AuthenticationResponse.success(authenticationRequest.getIdentity(), Collections.singletonMap("email", "john@email.com"));
    }
}

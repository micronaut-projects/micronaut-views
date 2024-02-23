package io.micronaut.views.model.security

import io.micronaut.core.annotation.Nullable
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.AuthenticationRequest
import io.micronaut.security.authentication.AuthenticationResponse
import io.micronaut.security.authentication.provider.HttpRequestAuthenticationProvider
import jakarta.inject.Singleton

@Singleton
class MockAuthenticationProvider<B> implements HttpRequestAuthenticationProvider<B> {

    @Override
    AuthenticationResponse authenticate(@Nullable HttpRequest<B> httpRequest, AuthenticationRequest<String, String> authenticationRequest) {
        AuthenticationResponse.success(authenticationRequest.getIdentity().toString(), [email: "john@email.com"])
    }
}

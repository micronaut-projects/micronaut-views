package io.micronaut.views.model.security

import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.AuthenticationRequest
import io.micronaut.security.authentication.AuthenticationResponse
import io.micronaut.security.authentication.provider.HttpRequestAuthenticationProvider
import jakarta.inject.Singleton

@Singleton
class MockAuthenticationProvider<B> : HttpRequestAuthenticationProvider<B> {

    override fun authenticate(
        requestContext: HttpRequest<B>,
        authRequest: AuthenticationRequest<String, String>
    ): AuthenticationResponse = AuthenticationResponse.success(authRequest.identity, mapOf("email" to "john@email.com"))
}

package io.micronaut.views.model.security

import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.AuthenticationRequest
import io.micronaut.security.authentication.AuthenticationResponse
import io.micronaut.security.authentication.provider.AuthenticationProvider
import jakarta.inject.Singleton

@Singleton
class MockAuthenticationProvider : AuthenticationProvider<HttpRequest<*>, Any, Any> {

    override fun authenticate(requestContext: HttpRequest<*>, authRequest: AuthenticationRequest<Any, Any>): AuthenticationResponse =
        AuthenticationResponse.success(
            authRequest.identity.toString(),
            mapOf("email" to "john@email.com")
        )
}

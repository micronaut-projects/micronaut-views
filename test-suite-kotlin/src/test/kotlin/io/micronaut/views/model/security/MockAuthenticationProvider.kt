package io.micronaut.views.model.security

import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.AuthenticationProvider
import io.micronaut.security.authentication.AuthenticationRequest
import io.micronaut.security.authentication.AuthenticationResponse
import jakarta.inject.Singleton
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink

@Singleton
class MockAuthenticationProvider : AuthenticationProvider<HttpRequest<*>> {

    override fun authenticate(httpRequest: HttpRequest<*>?, authenticationRequest: AuthenticationRequest<*, *>) =
        Flux.create(
            { emitter: FluxSink<AuthenticationResponse> ->
                emitter.next(
                    AuthenticationResponse.success(
                        authenticationRequest.identity.toString(),
                        mapOf("email" to "john@email.com")
                    )
                )
                emitter.complete()
            },
            FluxSink.OverflowStrategy.ERROR
        )
}

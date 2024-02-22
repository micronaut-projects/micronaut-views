package io.micronaut.views.model.security;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Collections;

@Singleton
public class MockAuthenticationProvider implements AuthenticationProvider<HttpRequest<?>> {
    @Override
    public Publisher<AuthenticationResponse> authenticate(@Nullable HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authenticationRequest) {
        return Flux.create(emitter -> {
            emitter.next(AuthenticationResponse.success(authenticationRequest.getIdentity().toString(), Collections.singletonMap("email", "john@email.com")));
            emitter.complete();
        }, FluxSink.OverflowStrategy.ERROR);
    }
}

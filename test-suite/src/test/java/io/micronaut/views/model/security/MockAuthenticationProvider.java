package io.micronaut.views.model.security;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Flux;
import org.reactivestreams.Publisher;

import jakarta.inject.Singleton;
import java.util.Collections;

@Singleton
public class MockAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Publisher<AuthenticationResponse> authenticate(@Nullable HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authenticationRequest) {
        return Flux.create(emitter -> {
            UserDetailsEmail userDetailsEmail = new UserDetailsEmail(authenticationRequest.getIdentity().toString(), Collections.emptyList(), "john@email.com");
            emitter.next(userDetailsEmail);
            emitter.complete();
        }, FluxSink.OverflowStrategy.ERROR);
    }
}

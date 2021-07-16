package io.micronaut.views.model.security;

import edu.umd.cs.findbugs.annotations.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import jakarta.inject.Singleton;
import java.util.Collections;

@Singleton
public class MockAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Publisher<AuthenticationResponse> authenticate(@Nullable HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authenticationRequest) {
        return Flowable.create(emitter -> {
            UserDetailsEmail userDetailsEmail = new UserDetailsEmail(authenticationRequest.getIdentity().toString(), Collections.emptyList(), "john@email.com");
            emitter.onNext(userDetailsEmail);
            emitter.onComplete();
        }, BackpressureStrategy.ERROR);
    }
}

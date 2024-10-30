package io.micronaut.views.tests.security.csrf;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.*;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestAuthenticationProvider;
import io.micronaut.security.csrf.repository.CsrfTokenRepository;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.View;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Property(name = "micronaut.security.authentication", value = "session")
@Property(name = "micronaut.security.redirect.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.security.csrf.filter.regex-pattern", value = "^(?!\\/login).*$")
@Property(name = "micronaut.security.csrf.signature-key", value = "pleaseChangeThisSecretForANewOnekoqQ-EstJQLr_T-1qS0gZH75aKtMN3Yj0iPS4hcgUuTwjAzZr1Z9CAow")
@Property(name = "spec.name", value = "CsrfSessionLogingHandlerTest")
@MicronautTest
class CsrfSessionLogingHandlerTest {

    @Test
    void loginSavesACsrfTokenInSession(@Client("/") HttpClient httpClient, CsrfTokenRepository<HttpRequest<?>> csrfTokenRepository) {
        BlockingHttpClient client = httpClient.toBlocking();
        HttpRequest<?> loginRequest = HttpRequest.POST("/login",Map.of("username",  "sherlock", "password", "password"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_TYPE);

        HttpResponse<?> loginRsp = assertDoesNotThrow(() -> client.exchange(loginRequest));
        assertEquals(HttpStatus.OK, loginRsp.getStatus());
        String cookie = loginRsp.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertNotNull(cookie);
        assertTrue(cookie.contains("SESSION="));
        assertTrue(cookie.contains("; HTTPOnly"));
        String sessionId = cookie.split(";")[0].split("=")[1];
        assertNotNull(sessionId);
        HttpRequest<?> csrfEchoRequestWithSession = HttpRequest.GET("/csrf/echo")
                .contentType(MediaType.TEXT_HTML_TYPE)
                .cookie(Cookie.of("SESSION", sessionId));
        String html = assertDoesNotThrow(() -> client.retrieve(csrfEchoRequestWithSession));
        assertFalse(html.contains("<meta name=\"csrf-token\" />"));

        // request the page without session and no csrf token is present
        HttpRequest<?> csrfEchoRequestWithoutSession = HttpRequest.GET("/csrf/echo")
                .contentType(MediaType.TEXT_HTML_TYPE);
        html = assertDoesNotThrow(() -> client.retrieve(csrfEchoRequestWithoutSession));
        assertTrue(html.contains("<meta name=\"csrf-token\" />"));
    }

    @Requires(property = "spec.name", value = "CsrfSessionLogingHandlerTest")
    @Singleton
    static class AuthenticationProviderUserPassword<B> implements HttpRequestAuthenticationProvider<B> {
        @Override
        public @NonNull AuthenticationResponse authenticate(@Nullable HttpRequest<B> requestContext, @NonNull AuthenticationRequest<String, String> authRequest) {
            return AuthenticationResponse.success("sherlock");
        }
    }

    @Requires(property = "spec.name", value = "CsrfSessionLogingHandlerTest")
    @Controller("/csrf")
    static class CsrfTokenEchoController {
        @Secured(SecurityRule.IS_ANONYMOUS)
        @Produces(MediaType.TEXT_HTML)
        @Get("/echo")
        @View("index")
        Map<String, Object> index() {
            return Collections.emptyMap();
        }
    }
}
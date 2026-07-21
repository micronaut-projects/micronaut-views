package io.micronaut.views.jinjava;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientException;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
@Property(name = "spec.name", value = "jinjava")
@Property(name = "micronaut.security.enabled", value = "false")
class JinjavaViewsRendererTest {

    @Inject
    ApplicationContext applicationContext;

    @Inject
    @Client("/")
    HttpClient httpClient;

    @Test
    void beanIsLoaded() {
        assertNotNull(applicationContext.getBean(JinjavaViewsRenderer.class));
    }

    @Test
    void rendersAModelAndAPojoWithJinjaSyntax() {
        assertTrue(httpClient.toBlocking().retrieve("/jinjava").contains("<h1>username: <span>sdelamo</span></h1>"));
        assertTrue(httpClient.toBlocking().retrieve("/jinjava/pojo").contains("<h1>username: <span>sdelamo</span></h1>"));
    }

    @Test
    void rendersIncludedAndInheritedTemplates() {
        assertTrue(httpClient.toBlocking().retrieve("/jinjava").contains("<title>Jinjava Home</title>"));
    }

    @Test
    void aMissingViewFailsRendering() {
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
            () -> httpClient.toBlocking().retrieve("/jinjava/bogus"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
    }

    @Test
    void anInvalidViewFailsRendering() {
        assertThrows(HttpClientException.class, () -> httpClient.toBlocking().retrieve("/jinjava/badsyntax"));
    }
}

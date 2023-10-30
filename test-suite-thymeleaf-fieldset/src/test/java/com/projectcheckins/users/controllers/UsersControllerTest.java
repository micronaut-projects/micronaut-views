package com.projectcheckins.users.controllers;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class UsersControllerTest {

    @Test
    void registerUserWithPasswordNotMatching(@Client("/") HttpClient httpClient) {
        BlockingHttpClient client = httpClient.toBlocking();
        String body = "username=sdelamo&email=sergio.delamo%40softamo.com&password=foo123&confirmPassword=foobar";
        HttpRequest<?> request = HttpRequest.POST("/users/save", body).contentType(MediaType.APPLICATION_FORM_URLENCODED_TYPE).accept(MediaType.TEXT_HTML);
        Executable e = () -> client.exchange(request);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, e);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, thrown.getStatus());
    }
}
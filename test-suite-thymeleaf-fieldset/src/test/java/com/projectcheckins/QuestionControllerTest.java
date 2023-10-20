package com.projectcheckins;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class QuestionControllerTest {

    @Test
    void submitForm(@Client("/") HttpClient httpClient) {
        BlockingHttpClient client = httpClient.toBlocking();
        String bodyWithMultipleUsers = "question=en+que+trabajas&onceAWeekOn=MONDAY&timeOfDay=09%3A20&usersId=1&usersId=2";
        assertDoesNotThrow(() -> client.exchange(createRequest(bodyWithMultipleUsers)));
        String bodyWithOneUser = "question=En+que+trabajas&onceAWeekOn=THURSDAY&timeOfDay=09%3A30&usersId=2";
        assertDoesNotThrow(() -> client.exchange(createRequest(bodyWithOneUser)));
    }

    private HttpRequest<?> createRequest(String body) {
        return HttpRequest.POST("/questions/save", body).contentType(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
    }
}

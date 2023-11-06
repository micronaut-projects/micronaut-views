package io.micronaut.views.fields.thymeleaf;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static io.micronaut.views.fields.thymeleaf.TestUtils.htmlGet;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "spec.name", value = "BookControllerTest")
@MicronautTest
class UsersControllerTest {

    @Test
    void itIsPossibleToSave(@Client("/") HttpClient httpClient) {
        BlockingHttpClient client = httpClient.toBlocking();
        String html = assertDoesNotThrow(() -> client.retrieve(htmlGet("/users/auth")));
        assertTrue(html.contains("<!DOCTYPE html>"));
        assertTrue(html.contains("""
                <form action="/login" method="post">\
                <div class="mb-3">\
                <label for="username" class="form-label">Username</label>\
                <input type="text" name="username" value="" id="username" class="form-control" required="required"/>\
                </div>\
                <div class="mb-3">\
                <label for="password" class="form-label">Password</label>\
                <input type="password" name="password" value="" id="password" class="form-control" required="required"/>\
                </div>\
                <input type="submit" value="Submit" class="btn btn-primary"/>\
                </form>"""));
    }
}

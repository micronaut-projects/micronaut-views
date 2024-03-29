package example.micronaut.test;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class TemplateTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void testIndex() {
        HttpRequest<?> request = HttpRequest.GET("/");
        HttpResponse<String> response = client.toBlocking().exchange(request, String.class);
        assertNotNull(response);
        Optional<String> body = response.getBody();
        assertTrue(body.isPresent());
        assertTrue(body.get().contains("Title - Index"));
        assertTrue(body.get().contains("<img src=\"images/micronaut_mini_copy_tm.svg\" width=\"400\"/>"));
    }

    @Test
    void testLink() {
        HttpRequest<?> request = HttpRequest.GET("/folder1/link1.html");
        HttpResponse<String> response = client.toBlocking().exchange(request, String.class);
        assertNotNull(response);
        Optional<String> body = response.getBody();
        assertTrue(body.isPresent());
        assertTrue(body.get().contains("Title - Link"));
        assertTrue(body.get().contains("<a href=\"https://micronaut.io/documentation.html\">Micronaut documentation</a>"));
    }

    @Test
    void testModelView() {
        HttpResponse<String> response = client.toBlocking().exchange(HttpRequest.GET("/views/model"), String.class);
        assertNotNull(response);
        Optional<String> body = response.getBody();
        assertTrue(body.isPresent());
        assertTrue(body.get().contains("Title - Template"));
        assertTrue(body.get().contains("username: <span>test-user</span>"));
    }

    @Test
    void testPojoView() {
        HttpResponse<String> response = client.toBlocking().exchange(HttpRequest.GET("/views/pojo"), String.class);
        assertNotNull(response);
        Optional<String> body = response.getBody();
        assertTrue(body.isPresent());
        assertTrue(body.get().contains("Title - Template"));
        assertTrue(body.get().contains("username: <span>test-user</span>"));
    }
}

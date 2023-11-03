package io.micronaut.views.fields.thymeleaf;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Property(name = "datasources.default.password", value = "")
@Property(name = "datasources.default.dialect", value = "H2")
@Property(name = "datasources.default.schema-generate", value = "CREATE_DROP")
@Property(name = "datasources.default.url", value = "jdbc:h2:mem:devDb;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE")
@Property(name = "datasources.default.username", value = "sa")
@Property(name = "datasources.default.driver-class-name=org.h2.Driver")
@Property(name = "spec.name", value = "BookControllerTest")
@MicronautTest
class BookControllerTest {

    @Test
    void itIsPossibleToSave(@Client("/") HttpClient httpClient) {
        BlockingHttpClient client = httpClient.toBlocking();
        String html = assertDoesNotThrow(() -> client.retrieve(htmlGet("/books/list")));
        assertTrue(html.contains("<!DOCTYPE html>"));
        assertFalse(html.contains("<li>"));

        html = assertDoesNotThrow(() -> client.retrieve(htmlGet("/books/create")));
        assertTrue(html.contains("""
                <form action="/books/save" method="post"><div class="mb-3"><label for="title" class="form-label">Title</label><input type="text" name="title" value="" id="title" class="form-control" required="required"/></div><div class="mb-3"><label for="pages" class="form-label">Pages</label><input type="number" name="pages" value="" id="pages" min="1" class="form-control" required="required"/></div><input type="submit" value="Submit" class="btn btn-primary"/></form>"""));
        assertFalse(html.contains("<li>"));

        html = assertDoesNotThrow(() -> client.retrieve(formPost("/books/save", "title=Building Microservices&pages=120")));
        assertTrue(html.contains("<!DOCTYPE html>"));
        assertTrue(html.contains("<li><span>Building Microservices</span><span>120</span></li>"));
    }

    private static HttpRequest<?> htmlGet(String path) {
        return HttpRequest.GET(path).accept(MediaType.TEXT_HTML);
    }

    private static HttpRequest<?> formPost(String path, String body) {
        return HttpRequest.POST(path, body).contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.TEXT_HTML);
    }
}

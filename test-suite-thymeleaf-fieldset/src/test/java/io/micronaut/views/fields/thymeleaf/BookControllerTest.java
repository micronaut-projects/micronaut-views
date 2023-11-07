package io.micronaut.views.fields.thymeleaf;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static io.micronaut.views.fields.thymeleaf.TestUtils.formPost;
import static io.micronaut.views.fields.thymeleaf.TestUtils.htmlGet;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
                <form action="/books/save" method="post">\
                <div class="mb-3">\
                <label for="title" class="form-label">Title</label>\
                <input type="text" name="title" value="" id="title" minlength="2" maxlength="255" class="form-control" required="required"/>\
                </div>\
                <div class="mb-3">\
                <label for="pages" class="form-label">Pages</label>\
                <input type="number" name="pages" value="" id="pages" min="1" max="21450" class="form-control" required="required"/>\
                </div>\
                <input type="submit" value="Submit" class="btn btn-primary"/>\
                </form>"""));
        assertFalse(html.contains("<li>"));

        html = assertDoesNotThrow(() -> client.retrieve(formPost("/books/save", "title=Building Microservices&pages=120")));
        assertTrue(html.contains("<!DOCTYPE html>"));
        assertTrue(html.contains("<li><span>Building Microservices</span><span>120</span></li>"));
    }

    @Test
    void formIsValidated(@Client("/") HttpClient httpClient) {
        BlockingHttpClient client = httpClient.toBlocking();
        Argument<String> okResponse = Argument.of(String.class);
        Argument<String> failureResponse = Argument.of(String.class);
        HttpRequest<?> request = formPost("/books/save", "title=Building Microservices&pages=0");

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.retrieve(request, okResponse, failureResponse));
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatus());
        Optional<String> htmlOptional = e.getResponse().getBody(String.class);
        assertTrue(htmlOptional.isPresent());
        String html = htmlOptional.get();
        assertTrue(html.contains("""
                <form action="/books/save" method="post">\
                <div class="mb-3">\
                <label for="title" class="form-label">Title</label>\
                <input type="text" name="title" value="Building Microservices" id="title" minlength="2" maxlength="255" class="form-control" required="required"/>\
                </div>\
                <div class="mb-3">\
                <label for="pages" class="form-label">Pages</label><input type="number" name="pages" value="0" id="pages" min="1" max="21450" class="form-control is-invalid" aria-describedby="pagesValidationServerFeedback" required="required"/>\
                <div id="pagesValidationServerFeedback" class="invalid-feedback">must be greater than or equal to 1</div>\
                </div>\
                <input type="submit" value="Submit" class="btn btn-primary"/>\
                </form>"""));

    }
}

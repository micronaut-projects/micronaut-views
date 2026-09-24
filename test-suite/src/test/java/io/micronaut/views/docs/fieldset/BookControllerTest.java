package io.micronaut.views.docs.fieldset;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@Property(name = "spec.name", value = "BookControllerTest")
@MicronautTest
class BookControllerTest {

    @Inject
    @Client("/")
    HttpClient httpClient;

    @Test
    void theFormIsGeneratedValidatedAndSaved() {
        BlockingHttpClient client = httpClient.toBlocking();
        String html = client.retrieve(htmlGet("/books/create"));
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

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class,
            () -> client.retrieve(formPost("/books/save", "title=Building Microservices&pages=0")));
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatus());
        Optional<String> htmlOptional = e.getResponse().getBody(String.class);
        assertTrue(htmlOptional.isPresent());
        assertTrue(htmlOptional.get().contains("<input type=\"number\" name=\"pages\" value=\"0\" id=\"pages\" min=\"1\" max=\"21450\" class=\"form-control is-invalid\" aria-describedby=\"pagesValidationServerFeedback\" required=\"required\"/>"));
        assertTrue(htmlOptional.get().contains("<div id=\"pagesValidationServerFeedback\" class=\"invalid-feedback\">must be greater than or equal to 1</div>"));

        html = client.retrieve(htmlGet("/books/list"));
        assertFalse(html.contains("<li>"));

        html = client.retrieve(formPost("/books/save", "title=Building Microservices&pages=120"));
        assertTrue(html.contains("<li><span>Building Microservices</span><span>120</span></li>"));
    }

    private static HttpRequest<?> htmlGet(String path) {
        return HttpRequest.GET(path).accept(MediaType.TEXT_HTML);
    }

    private static HttpRequest<?> formPost(String path, String body) {
        return HttpRequest.POST(path, body).contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.TEXT_HTML);
    }
}

package io.micronaut.views.fields;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.controllers.createsave.CreateHtmlResource;
import io.micronaut.views.fields.controllers.createsave.SaveService;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Property(name = "micronaut.http.client.follow-redirects", value = StringUtils.FALSE)
@Property(name = "spec.name", value = "CreateSaveControllerTest")
@MicronautTest
public class CreateSaveControllerTest {
    @Test
    void createControllerTest(@Client("/") HttpClient httpClient) {
        BlockingHttpClient client = httpClient.toBlocking();
        HttpRequest<?> request = HttpRequest.GET("/contact/create").accept(MediaType.TEXT_HTML);
        HttpResponse<String> responseHtml = assertDoesNotThrow(() -> client.exchange(request, String.class));
        assertTrue(responseHtml.getContentType().isPresent());
        assertEquals(MediaType.TEXT_HTML_TYPE, responseHtml.getContentType().get());
        String html = responseHtml.body();
        assertNotNull(html);
        assertEquals("<!DOCTYPE html><html><head><title>Save Contact</title></head><body><form action=\"/contact/save\" method=\"POST\">" +
            "<label for=\"firstName\">First Name</label><input type=\"text\" name=\"firstName\" id=\"firstName\" required/>" +
            "<label for=\"lastName\">Last Name</label><input type=\"text\" name=\"lastName\" id=\"lastName\" required/>" +
            "<label for=\"email\">Email</label><input type=\"email\" name=\"email\" id=\"email\" required/>" +
            "<input type=\"submit\" value=\"Create\"/></form></body></html>", html);
    }

    @Test
    void saveControllerTest(@Client("/") HttpClient httpClient) {
        BlockingHttpClient client = httpClient.toBlocking();
        HttpRequest<?> request = HttpRequest.POST(UriBuilder.of("/contact").path("save").build(),
            Map.of("firstName", "Sergio", "lastName", "del Amo", "email", "delamos@unityfoundation.io"))
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
        HttpResponse<?> response = assertDoesNotThrow(() -> client.exchange(request));
        assertEquals(HttpStatus.SEE_OTHER, response.getStatus());
    }

    @Test
    void saveControllerValidationTest(@Client("/") HttpClient httpClient) {
        BlockingHttpClient client = httpClient.toBlocking();
        HttpRequest<?> request = HttpRequest.POST(UriBuilder.of("/contact").path("save").build(),
                Map.of("firstName", "Sergio", "lastName", "del Amo", "email", "delamos"))
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.exchange(request, String.class));
        HttpResponse<?> response = e.getResponse();
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatus());
        assertTrue(response.getContentType().isPresent());
        assertEquals(MediaType.TEXT_HTML_TYPE, response.getContentType().get());
        Optional<String> htmlOptional = response.getBody(String.class);
        assertTrue(htmlOptional.isPresent());
        String html = htmlOptional.get();
        assertNotNull(html);
        assertEquals("<!DOCTYPE html><html><head><title>Save Contact</title></head><body><form action=\"/contact/save\" method=\"POST\">" +
            "<label for=\"firstName\">First Name</label><input type=\"text\" name=\"firstName\" id=\"firstName\" value=\"Sergio\" required/>" +
            "<label for=\"lastName\">Last Name</label><input type=\"text\" name=\"lastName\" id=\"lastName\" value=\"del Amo\" required/>" +
            "<label for=\"email\">Email</label><input type=\"email\" name=\"email\" id=\"email\" value=\"delamos\" required/><div>must be a well-formed email address</div>" +
            "<input type=\"submit\" value=\"Create\"/></form></body></html>", html);
    }

    @Requires(property = "spec.name", value = "CreateSaveControllerTest")
    @Named("contact")
    @Singleton
    static class ContactResources implements CreateHtmlResource<Contact> {
        @Override
        public Class<Contact> createClass() {
            return Contact.class;
        }
    }

    @Requires(property = "spec.name", value = "CreateSaveControllerTest")
    @Named("contact")
    @Singleton
    static class ContactSaveService implements SaveService<Contact> {
        @Override
        public URI save(@NonNull @NotNull @Valid Contact form) {
            return URI.create("/contact/list");
        }
    }

    @Introspected
    record Contact(@NotBlank String firstName, @NotBlank String lastName, @NotBlank @Email String email) {
    }


}

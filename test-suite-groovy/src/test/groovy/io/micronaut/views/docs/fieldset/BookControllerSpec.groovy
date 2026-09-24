package io.micronaut.views.docs.fieldset

import io.micronaut.context.annotation.Property
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@Property(name = "spec.name", value = "BookControllerTest")
@MicronautTest
class BookControllerSpec extends Specification {

    @Inject
    @Client("/")
    HttpClient httpClient

    void "the form is generated, validated and saved"() {
        given:
        def client = httpClient.toBlocking()

        when:
        String html = client.retrieve(htmlGet("/books/create"))

        then:
        html.contains('<form action="/books/save" method="post">' +
                '<div class="mb-3">' +
                '<label for="title" class="form-label">Title</label>' +
                '<input type="text" name="title" value="" id="title" minlength="2" maxlength="255" class="form-control" required="required"/>' +
                '</div>' +
                '<div class="mb-3">' +
                '<label for="pages" class="form-label">Pages</label>' +
                '<input type="number" name="pages" value="" id="pages" min="1" max="21450" class="form-control" required="required"/>' +
                '</div>' +
                '<input type="submit" value="Submit" class="btn btn-primary"/>' +
                '</form>')

        when:
        client.retrieve(formPost("/books/save", "title=Building Microservices&pages=0"))

        then:
        HttpClientResponseException e = thrown()
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        String errorHtml = e.response.getBody(String).orElseThrow()
        errorHtml.contains('<input type="number" name="pages" value="0" id="pages" min="1" max="21450" class="form-control is-invalid" aria-describedby="pagesValidationServerFeedback" required="required"/>')
        errorHtml.contains('<div id="pagesValidationServerFeedback" class="invalid-feedback">must be greater than or equal to 1</div>')

        when:
        html = client.retrieve(htmlGet("/books/list"))

        then:
        !html.contains("<li>")

        when:
        html = client.retrieve(formPost("/books/save", "title=Building Microservices&pages=120"))

        then:
        html.contains("<li><span>Building Microservices</span><span>120</span></li>")
    }

    private static HttpRequest<?> htmlGet(String path) {
        HttpRequest.GET(path).accept(MediaType.TEXT_HTML)
    }

    private static HttpRequest<?> formPost(String path, String body) {
        HttpRequest.POST(path, body).contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.TEXT_HTML)
    }
}

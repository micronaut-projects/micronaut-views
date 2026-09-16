package io.micronaut.views.docs.fieldset

import io.micronaut.context.annotation.Property
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@Property(name = "spec.name", value = "BookControllerTest")
@MicronautTest
internal class BookControllerTest {

    @Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @Test
    fun theFormIsGeneratedValidatedAndSaved() {
        val client = httpClient.toBlocking()
        var html = client.retrieve(htmlGet("/books/create"))
        assertTrue(html.contains("""<form action="/books/save" method="post"><div class="mb-3"><label for="title" class="form-label">Title</label><input type="text" name="title" value="" id="title" minlength="2" maxlength="255" class="form-control" required="required"/></div><div class="mb-3"><label for="pages" class="form-label">Pages</label><input type="number" name="pages" value="" id="pages" min="1" max="21450" class="form-control" required="required"/></div><input type="submit" value="Submit" class="btn btn-primary"/></form>"""))

        val e = assertThrows(HttpClientResponseException::class.java) {
            client.retrieve(formPost("/books/save", "title=Building Microservices&pages=0"))
        }
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.status)
        val errorHtml = e.response.getBody(String::class.java).orElseThrow()
        assertTrue(errorHtml.contains("""<input type="number" name="pages" value="0" id="pages" min="1" max="21450" class="form-control is-invalid" aria-describedby="pagesValidationServerFeedback" required="required"/>"""))
        assertTrue(errorHtml.contains("""<div id="pagesValidationServerFeedback" class="invalid-feedback">must be greater than or equal to 1</div>"""))

        html = client.retrieve(htmlGet("/books/list"))
        assertFalse(html.contains("<li>"))

        html = client.retrieve(formPost("/books/save", "title=Building Microservices&pages=120"))
        assertTrue(html.contains("<li><span>Building Microservices</span><span>120</span></li>"))
    }

    private fun htmlGet(path: String): HttpRequest<*> = HttpRequest.GET<Any>(path).accept(MediaType.TEXT_HTML)

    private fun formPost(path: String, body: String): HttpRequest<*> =
        HttpRequest.POST(path, body).contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.TEXT_HTML)
}

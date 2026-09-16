from typing import Annotated

from jakarta.inject import Inject
from java.lang import String
from micronaut.context.annotation import Property
from micronaut.http import HttpRequest, HttpStatus, MediaType
from micronaut.http.client import HttpClient
from micronaut.http.client.annotation import Client
from micronaut.http.client.exceptions import HttpClientResponseException
from micronaut.test.extensions.junit5.annotation import MicronautTest
from org.junit.jupiter.api import Test


@Property(name="micronaut.views.soy.enabled", value="false")
@Property(name="micronaut.security.enabled", value="false")
@Property(name="spec.name", value="BookControllerTest")
@MicronautTest
class BookControllerTest:
    httpClient: Annotated[HttpClient, Inject, Client("/")]

    @Test
    def test_the_form_is_generated_validated_and_saved(self):
        client = self.httpClient.toBlocking()
        html = client.retrieve(self.html_get("/books/create"))
        assert ('<form action="/books/save" method="post">'
                '<div class="mb-3">'
                '<label for="title" class="form-label">Title</label>'
                '<input type="text" name="title" value="" id="title" minlength="2" maxlength="255" class="form-control" required="required"/>'
                '</div>'
                '<div class="mb-3">'
                '<label for="pages" class="form-label">Pages</label>'
                '<input type="number" name="pages" value="" id="pages" min="1" max="21450" class="form-control" required="required"/>'
                '</div>'
                '<input type="submit" value="Submit" class="btn btn-primary"/>'
                '</form>') in html

        try:
            client.retrieve(self.form_post("/books/save", "title=Building Microservices&pages=0"))
            assert False, "HttpClientResponseException expected"
        except HttpClientResponseException as e:
            assert e.getStatus() == HttpStatus.UNPROCESSABLE_ENTITY
            error_html = e.getResponse().getBody(String).orElseThrow()
            assert '<input type="number" name="pages" value="0" id="pages" min="1" max="21450" class="form-control is-invalid" aria-describedby="pagesValidationServerFeedback" required="required"/>' in error_html
            assert '<div id="pagesValidationServerFeedback" class="invalid-feedback">must be greater than or equal to 1</div>' in error_html

        html = client.retrieve(self.html_get("/books/list"))
        assert "<li>" not in html

        html = client.retrieve(self.form_post("/books/save", "title=Building Microservices&pages=120"))
        assert "<li><span>Building Microservices</span><span>120</span></li>" in html

    def html_get(self, path: str):
        return HttpRequest.GET(path).accept(MediaType.TEXT_HTML)

    def form_post(self, path: str, body: str):
        return HttpRequest.POST(path, body).contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.TEXT_HTML)

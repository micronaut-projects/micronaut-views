from typing import Annotated

from jakarta.inject import Inject
from java.lang import String
from micronaut.context.annotation import Property
from micronaut.http import HttpRequest
from micronaut.http.client import HttpClient
from micronaut.http.client.annotation import Client
from micronaut.test.extensions.junit5.annotation import MicronautTest
from org.junit.jupiter.api import Test


@Property(name="micronaut.views.soy.enabled", value="false")
@Property(name="micronaut.security.enabled", value="false")
@Property(name="spec.name", value="ViewsControllerTest")
@MicronautTest
class ViewsControllerTest:
    httpClient: Annotated[HttpClient, Inject, Client("/")]

    @Test
    def test_a_view_can_be_rendered_from_a_map_a_pojo_or_a_model_and_view(self):
        client = self.httpClient.toBlocking()
        for path in ["/views", "/views/pojo", "/views/modelAndView"]:
            html = client.retrieve(HttpRequest.GET(path), String)
            assert "<h1>username: <span>sdelamo</span></h1>" in html, path

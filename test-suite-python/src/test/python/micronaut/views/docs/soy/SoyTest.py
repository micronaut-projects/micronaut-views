from typing import Annotated

from jakarta.inject import Inject
from java.lang import String
from micronaut.context.annotation import Property
from micronaut.http import HttpStatus
from micronaut.http.client import HttpClient
from micronaut.http.client.annotation import Client
from micronaut.test.extensions.junit5.annotation import MicronautTest
from org.junit.jupiter.api import Test


@Property(name="micronaut.security.enabled", value="false")
@Property(name="spec.name", value="soy")
@Property(name="micronaut.views.velocity.enabled", value="false")
@MicronautTest
class SoyTest:
    httpClient: Annotated[HttpClient, Inject, Client("/")]

    @Test
    def test_invoking_soy_renders_soy_template_from_a_controller_returning_a_map(self):
        rsp = self.httpClient.toBlocking().exchange("/soy", String)
        assert rsp.status() == HttpStatus.OK
        body = rsp.body()
        assert body is not None
        assert "<h1>username: <span>sgammon</span></h1>" in body

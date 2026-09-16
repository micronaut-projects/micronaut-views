from typing import Annotated

from jakarta.inject import Inject
from java.lang import String
from micronaut.context.annotation import Property
from micronaut.http import HttpRequest, HttpStatus
from micronaut.http.client import HttpClient
from micronaut.http.client.annotation import Client
from micronaut.test.extensions.junit5.annotation import MicronautTest
from org.junit.jupiter.api import Test


@Property(name="spec.name", value="SecurityViewModelProcessorSpec")
@Property(name="micronaut.views.soy.enabled", value="false")
@Property(name="micronaut.security.views-model-decorator.security-key", value="securitycustom")
@MicronautTest
class CustomSecurityKeyTest:
    httpClient: Annotated[HttpClient, Inject, Client("/")]

    @Test
    def test_a_custom_security_property_name_can_be_injected_to_the_model(self):
        client = self.httpClient.toBlocking()
        response = client.exchange(HttpRequest.GET("/").basicAuth("john", "secret"), String)
        assert response.status() == HttpStatus.OK
        html = response.body()
        assert html is not None
        assert "User: john" not in html
        assert "Custom: john" in html

from typing import Annotated

from jakarta.inject import Inject
from micronaut.context.annotation import Property
from micronaut.http.client import HttpClient
from micronaut.http.client.annotation import Client
from micronaut.test.extensions.junit5.annotation import MicronautTest
from org.junit.jupiter.api import Test


@Property(name="micronaut.security.enabled", value="false")
@Property(name="spec.name", value="soy")
@Property(name="micronaut.views.soy.renaming-enabled", value="true")
@Property(name="micronaut.views.velocity.enabled", value="false")
@MicronautTest
class SoyRenamingTest:
    httpClient: Annotated[HttpClient, Inject, Client("/")]

    @Test
    def test_css_classes_are_renamed_with_the_renaming_map(self):
        html = self.httpClient.toBlocking().retrieve("/soy/renaming")
        assert '<div class="a-b-c a-b-c-d">Renamed</div>' in html, html

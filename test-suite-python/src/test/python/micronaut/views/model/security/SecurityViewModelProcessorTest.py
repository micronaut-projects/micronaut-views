from typing import Annotated

import java
from jakarta.inject import Inject
from java.lang import String
from micronaut.context import BeanContext
from micronaut.context.annotation import Property
from micronaut.http import HttpRequest, HttpStatus
from micronaut.http.client import HttpClient
from micronaut.http.client.annotation import Client
from micronaut.test.extensions.junit5.annotation import MicronautTest
from org.junit.jupiter.api import Test

from .BooksController import BooksController
from .MockAuthenticationProvider import MockAuthenticationProvider

# TODO(python): java.type needed because the Python snippet package `micronaut/views/model/security` shadows the
# generated `micronaut.views.model.security` module, so `from micronaut.views.model.security import
# SecurityViewModelProcessor` resolves to the Python package (compiler fix merged, not yet released).
SecurityViewModelProcessor = java.type("io.micronaut.views.model.security.SecurityViewModelProcessor")


@Property(name="spec.name", value="SecurityViewModelProcessorSpec")
@Property(name="micronaut.views.soy.enabled", value="false")
@MicronautTest
class SecurityViewModelProcessorTest:
    httpClient: Annotated[HttpClient, Inject, Client("/")]
    beanContext: Annotated[BeanContext, Inject]

    @Test
    def test_by_default_security_views_model_decorator_bean_exists(self):
        assert self.beanContext.containsBean(SecurityViewModelProcessor)

    @Test
    def test_security_property_is_injected_to_the_model(self):
        assert self.beanContext.containsBean(BooksController)
        assert self.beanContext.containsBean(MockAuthenticationProvider)
        client = self.httpClient.toBlocking()
        request = HttpRequest.GET("/").basicAuth("john", "secret")
        response = client.exchange(request, String)
        assert response.status() == HttpStatus.OK
        html = response.body()
        assert html is not None
        assert "User: john email: john@email.com" in html
        assert "Developing Microservices" in html

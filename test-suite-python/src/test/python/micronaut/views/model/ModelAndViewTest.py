from typing import Annotated

from jakarta.inject import Inject
from java.lang import String
from micronaut.context import BeanContext
from micronaut.context.annotation import Property
from micronaut.http import HttpRequest, HttpStatus
from micronaut.http.client import HttpClient
from micronaut.http.client.annotation import Client
from micronaut.http.client.exceptions import HttpClientResponseException
from micronaut.test.extensions.junit5.annotation import MicronautTest
from org.junit.jupiter.api import Test

from .ConfigViewModelProcessor import ConfigViewModelProcessor
from .FruitsController import FruitsController


@Property(name="spec.name", value="ModelAndViewSpec")
@Property(name="micronaut.views.soy.enabled", value="false")
@Property(name="micronaut.security.views-model-decorator.enabled", value="false")
@Property(name="micronaut.application.name", value="test")
@Property(name="micronaut.security.enabled", value="false")
@MicronautTest
class ModelAndViewTest:
    httpClient: Annotated[HttpClient, Inject, Client("/")]
    beanContext: Annotated[BeanContext, Inject]

    @Test
    def test_a_view_model_can_be_any_object(self):
        client = self.httpClient.toBlocking()
        assert self.beanContext.containsBean(FruitsController)
        response = client.exchange(HttpRequest.GET("/"), String)
        assert response.status() == HttpStatus.OK
        html = response.body()
        assert html is not None
        assert "<h1>fruit: apple</h1>" in html
        assert "<h1>color: red</h1>" in html

    @Test
    def test_returning_a_null_model_causes_a_404(self):
        client = self.httpClient.toBlocking()
        try:
            client.exchange(HttpRequest.GET("/null"), String)
            assert False, "HttpClientResponseException expected"
        except HttpClientResponseException as e:
            assert e.getStatus() == HttpStatus.NOT_FOUND

    @Test
    def test_a_view_model_can_be_a_map(self):
        client = self.httpClient.toBlocking()
        response = client.exchange(HttpRequest.GET("/map"), String)
        assert response.status() == HttpStatus.OK
        html = response.body()
        assert "<h1>fruit: orange</h1>" in html
        assert "<h1>color: orange</h1>" in html

    @Test
    def test_models_can_be_dynamically_enhanced(self):
        client = self.httpClient.toBlocking()
        response = client.exchange(HttpRequest.GET("/processor"), String)
        assert response.status() == HttpStatus.OK
        html = response.body()
        assert self.beanContext.containsBean(ConfigViewModelProcessor)
        assert "<h1>config: test</h1>" in html

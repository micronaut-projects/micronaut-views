from typing import Annotated

from jakarta.inject import Inject
from micronaut.context.annotation import Property, Requires
from micronaut.http import HttpRequest, HttpResponse
from micronaut.http.annotation import Controller, Get, Post
from micronaut.http.client import HttpClient
from micronaut.http.client.annotation import Client
from micronaut.test.extensions.junit5.annotation import MicronautTest
from micronaut.views import ModelAndView
from micronaut.views.docs.turbo.Fruit import Fruit
from micronaut.views.htmx.http import HtmxRequestHeaders, HtmxResponse, HtmxResponseHeaders
from org.junit.jupiter.api import Test


@Property(name="micronaut.views.soy.enabled", value="false")
@Property(name="micronaut.security.enabled", value="false")
@Property(name="spec.name", value="HtmxRequestHeadersTest")
@MicronautTest
class HtmxTest:
    httpClient: Annotated[HttpClient, Inject, Client("/")]

    @Test
    def test_htmx_request_headers(self):
        client = self.httpClient.toBlocking()
        html = client.retrieve(HttpRequest.GET("/fruits/htmx").header("HX-Request", "true"))
        assert html == "<h1>fruit: Apple</h1>\n<h2>color: Red</h2>\n"
        html = client.retrieve(HttpRequest.GET("/fruits/htmx"))
        assert "<!DOCTYPE html>" in html

    @Test
    def test_out_of_band_swaps(self):
        client = self.httpClient.toBlocking()
        html = client.retrieve(HttpRequest.POST("/fruits/htmx", {}).header("HX-Request", "true"))
        assert html == "<h1>fruit: Apple</h1>\n<h2>color: Red</h2>\n<div id=\"message\" hx-swap-oob=\"true\">Swap me directly!</div>"

    @Test
    def test_htmx_response_headers(self):
        client = self.httpClient.toBlocking()
        response = client.exchange(HttpRequest.GET("/fruits/htmx/responseHeaders").header("HX-Request", "true"))
        assert response.getHeaders().get("HX-Refresh") == "true"


@Requires(property="spec.name", value="HtmxRequestHeadersTest")
@Controller("/fruits/htmx")
class HtmxRequestHeadersController:
    # tag::htmxRequestHeaders[]
    @Get
    def index(self, htmxRequestHeaders: HtmxRequestHeaders | None) -> ModelAndView:
        model = {"fruit": Fruit("Apple", "Red")}
        if htmxRequestHeaders is not None:
            return ModelAndView("fruit", model)
        return ModelAndView("fruits", model)
    # end::htmxRequestHeaders[]

    # tag::outOfBandSwaps[]
    @Post
    def out_of_band_swaps(self, htmxRequestHeaders: HtmxRequestHeaders) -> HtmxResponse:
        return (HtmxResponse.builder()
                .modelAndView(ModelAndView("fruit", {"fruit": Fruit("Apple", "Red")}))
                .modelAndView(ModelAndView("swap", {}))
                .build())
    # end::outOfBandSwaps[]

    # tag::htmxResponseHeaders[]
    @Get("/responseHeaders")
    def htmx_response_headers(self, htmxRequestHeaders: HtmxRequestHeaders) -> HttpResponse:
        return HttpResponse.ok().header(HtmxResponseHeaders.HX_REFRESH, "true")
    # end::htmxResponseHeaders[]

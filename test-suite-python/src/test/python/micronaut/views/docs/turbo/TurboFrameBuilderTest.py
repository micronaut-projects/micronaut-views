from typing import Annotated

from jakarta.inject import Inject
from java.lang import String
from micronaut.context.annotation import Property, Requires
from micronaut.http import HttpRequest, HttpResponse, MediaType
from micronaut.http.annotation import Controller, Get, Header, Produces
from micronaut.http.client import HttpClient
from micronaut.http.client.annotation import Client
from micronaut.test.extensions.junit5.annotation import MicronautTest
from micronaut.views import ModelAndView
from micronaut.views.turbo import TurboFrame
from micronaut.views.turbo.http import TurboHttpHeaders
from org.junit.jupiter.api import Test

from .Message import Message


@Property(name="micronaut.views.soy.enabled", value="false")
@Property(name="micronaut.security.enabled", value="false")
@Property(name="spec.name", value="TurboFrameBuilderTest")
@MicronautTest
class TurboFrameBuilderTest:
    httpClient: Annotated[HttpClient, Inject, Client("/")]

    @Test
    def test_you_can_use_turbo_frame_to_render_frame(self):
        client = self.httpClient.toBlocking()

        html = client.retrieve(HttpRequest.GET("/frame/builder").accept(MediaType.TEXT_HTML), String)
        assert "<h1>Editing message</h1>" in html
        assert "<turbo-frame id=\"message_1\">" in html
        assert "<form action=\"/messages/1\">" in html

        html = client.retrieve(HttpRequest.GET("/frame/builder")
                               .accept(MediaType.TEXT_HTML)
                               .header(TurboHttpHeaders.TURBO_FRAME, "message_1"), String)
        assert "<h1>Editing message</h1>" not in html
        assert "<turbo-frame id=\"message_1\">" in html
        assert "<form action=\"/messages/1\">" in html


@Requires(property="spec.name", value="TurboFrameBuilderTest")
@Controller("/frame")
class TurboFrameBuilderController:

    # tag::turboFrameBuilder[]
    @Produces(MediaType.TEXT_HTML)
    @Get("/builder")
    def index(self, turboFrame: Annotated[str | None, Header(TurboHttpHeaders.TURBO_FRAME)]) -> HttpResponse:
        message_id = 1
        model = {"message": Message(message_id, "My message title", "My message content")}
        return HttpResponse.ok(ModelAndView("edit", model) if turboFrame is None else
                               TurboFrame.builder()
                               .id("message_" + str(message_id))
                               .templateModel(model)
                               .templateView("form")).contentType(MediaType.TEXT_HTML)
    # end::turboFrameBuilder[]

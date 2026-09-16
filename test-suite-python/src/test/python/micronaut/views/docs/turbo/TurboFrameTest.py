from typing import Annotated

from jakarta.inject import Inject
from java.lang import String
from micronaut.context.annotation import Property, Requires
from micronaut.http import HttpRequest, HttpResponse, MediaType
from micronaut.http.annotation import Consumes, Controller, Get, Header, Part, Post, Produces
from micronaut.http.client import HttpClient
from micronaut.http.client.annotation import Client
from micronaut.http.client.multipart import MultipartBody
from micronaut.test.extensions.junit5.annotation import MicronautTest
from micronaut.views import ModelAndView, View
from micronaut.views.turbo import TurboFrame, TurboFrameView, TurboStreamView
from micronaut.views.turbo.http import TurboHttpHeaders, TurboMediaType
from org.junit.jupiter.api import Test

from .Message import Message


@Property(name="micronaut.views.soy.enabled", value="false")
@Property(name="micronaut.security.enabled", value="false")
@Property(name="spec.name", value="TurboFrameTest")
@MicronautTest
class TurboFrameTest:
    httpClient: Annotated[HttpClient, Inject, Client("/")]

    @Test
    def test_you_can_use_turbo_frame_to_render_frame(self):
        client = self.httpClient.toBlocking()
        html = client.retrieve(HttpRequest.GET("/frame").accept(MediaType.TEXT_HTML), String)
        assert "<h1>Editing message</h1>" in html
        assert "<turbo-frame id=\"message_1\">" in html
        assert "<form action=\"/messages/1\">" in html

        html = client.retrieve(HttpRequest.GET("/frame")
                               .accept(MediaType.TEXT_HTML)
                               .header(TurboHttpHeaders.TURBO_FRAME, "message_1"), String)
        assert "<h1>Editing message</h1>" not in html
        assert "<turbo-frame id=\"message_1\">" in html
        assert "<form action=\"/messages/1\">" in html

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

        body = (MultipartBody.builder()
                .addPart("id", "1")
                .addPart("title", "My new title")
                .addPart("body", "My new body")
                .build())
        html = client.retrieve(
            HttpRequest.POST("/frame/messages/1", body)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(TurboMediaType.TURBO_STREAM),
            String)
        assert html == ("<turbo-stream action=\"update\"><template><h1>My new title</h1>\n"
                        "<p>My new body</p>\n"
                        "</template></turbo-stream>")


@Requires(property="spec.name", value="TurboFrameTest")
@Controller("/frame")
class TurboFrameController:

    # tag::turboFrameView[]
    @Produces(MediaType.TEXT_HTML)
    @TurboFrameView("form")
    @View("edit")
    @Get
    def index(self) -> dict[str, object]:
        return {"message": Message(1, "My message title", "My message content")}
    # end::turboFrameView[]

    # tag::turboFramePost[]
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @TurboStreamView("view")
    @Produces(value=[MediaType.TEXT_HTML, TurboMediaType.TURBO_STREAM])
    @Post("/messages/{id}")
    def process_edit(self, id: Annotated[int, Part], title: Annotated[str, Part], body: Annotated[str, Part]) -> dict[str, object]:
        # Process the posted data, and return the updated message
        return {"message": Message(id, title, body)}
    # end::turboFramePost[]

    @Produces(MediaType.TEXT_HTML)
    @Get("/builder")
    def builder(self, turboFrame: Annotated[str | None, Header(TurboHttpHeaders.TURBO_FRAME)]) -> HttpResponse:
        message_id = 1
        model = {"message": Message(message_id, "My message title", "My message content")}
        return HttpResponse.ok(ModelAndView("edit", model) if turboFrame is None else
                               TurboFrame.builder()
                               .id("message_" + str(message_id))
                               .templateModel(model)
                               .templateView("form")).contentType(MediaType.TEXT_HTML)

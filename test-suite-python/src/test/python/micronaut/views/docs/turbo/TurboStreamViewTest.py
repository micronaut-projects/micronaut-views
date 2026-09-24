from typing import Annotated

from jakarta.inject import Inject
from java.lang import String
from micronaut.context.annotation import Property, Requires
from micronaut.http import HttpRequest, HttpStatus, MediaType
from micronaut.http.annotation import Controller, Get, Produces
from micronaut.http.client import HttpClient
from micronaut.http.client.annotation import Client
from micronaut.test.extensions.junit5.annotation import MicronautTest
from micronaut.views.turbo import TurboStreamAction, TurboStreamView
from micronaut.views.turbo.http import TurboHttpHeaders, TurboMediaType
from org.junit.jupiter.api import Test

from .Fruit import Fruit


@Property(name="micronaut.views.soy.enabled", value="false")
@Property(name="spec.name", value="TurboViewTest")
@Property(name="micronaut.security.enabled", value="false")
@MicronautTest
class TurboStreamViewTest:
    httpClient: Annotated[HttpClient, Inject, Client("/")]

    @Test
    def test_turbo_view(self):
        client = self.httpClient.toBlocking()

        # tag::turboviewrequest[]
        request = (HttpRequest.GET("/turbofruit")
                   .accept(TurboMediaType.TURBO_STREAM, MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML)
                   .header(TurboHttpHeaders.TURBO_FRAME, "dom_id"))
        # end::turboviewrequest[]

        response = client.exchange(request, String)

        assert response.status() == HttpStatus.OK
        assert response.getContentType().isPresent()
        assert response.getContentType().get().toString() == TurboMediaType.TURBO_STREAM
        assert response.body() == (
            # tag::turboviewresponse[]
            "<turbo-stream action=\"append\" target=\"dom_id\">"
            "<template>"
            "<h1>fruit: Banana</h1>\n"
            "<h2>color: Yellow</h2>\n"
            "</template>"
            "</turbo-stream>"
            # end::turboviewresponse[]
        )


@Requires(property="spec.name", value="TurboViewTest")
@Controller
class FruitController:

    # tag::turboview[]
    @Produces(value=[MediaType.TEXT_HTML, TurboMediaType.TURBO_STREAM])
    @TurboStreamView(value="fruit", action=TurboStreamAction.APPEND)
    @Get("/turbofruit")
    def show(self) -> dict[str, object]:
        return {"fruit": Fruit("Banana", "Yellow")}
    # end::turboview[]

package io.micronaut.views.docs.turbo

import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.Nullable
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Produces
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.views.ModelAndView
import io.micronaut.views.View
import io.micronaut.views.turbo.TurboFrame
import io.micronaut.views.turbo.TurboFrameView
import io.micronaut.views.turbo.http.TurboHttpHeaders
import jakarta.inject.Inject
import spock.lang.Specification

@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@Property(name = "spec.name", value = "TurboFrameTest")
@MicronautTest
class TurboFrameTest extends Specification {
    @Inject
    @Client("/")
    HttpClient httpClient

    void "you can use TurboFrameView annotation to render a Turbo Frame"() {
        given:
        BlockingHttpClient client = httpClient.toBlocking()

        when:
        String html = client.retrieve(HttpRequest.GET("/frame").accept(MediaType.TEXT_HTML), String)

        then:
        html.contains("<h1>Editing message</h1>")
        html.contains("<turbo-frame id=\"message_1\">")
        html.contains("<form action=\"/messages/1\">")

        when:
        html = client.retrieve(HttpRequest.GET("/frame")
                .accept(MediaType.TEXT_HTML)
                .header(TurboHttpHeaders.TURBO_FRAME, "message_1"), String)
        then:
        !html.contains("<h1>Editing message</h1>")
        html.contains("<turbo-frame id=\"message_1\">")
        html.contains("<form action=\"/messages/1\">")
    }

    @Requires(property = "spec.name", value = "TurboFrameTest")
    @Controller("/frame")
    static class TurboFrameController {
//tag::turboFrameView[]
@Produces(MediaType.TEXT_HTML)
@TurboFrameView("form")
@View("edit")
@Get
Map<String, Object> index() {
    ["message": new Message(id: 1L, name: "My message title", content: "My message content")]
}
//end::turboFrameView[]
    }

    static class Message {
        Long id
        String name
        String content
    }
}

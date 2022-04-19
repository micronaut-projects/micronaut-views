package io.micronaut.views.docs.turbo

import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.views.View
import io.micronaut.views.turbo.TurboFrameView
import io.micronaut.views.turbo.http.TurboHttpHeaders
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@Property(name = "spec.name", value = "TurboFrameTest")
@MicronautTest
class TurboFrameTest {
    @Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @Test
    fun youCanUseTurboFrameToRenderFrame() {
        val client = httpClient.toBlocking()
        var html = client.retrieve(
            HttpRequest.GET<Any>("/frame").accept(MediaType.TEXT_HTML),
            String::class.java
        )
        Assertions.assertTrue(html.contains("<h1>Editing message</h1>"))
        Assertions.assertTrue(html.contains("<turbo-frame id=\"message_1\">"))
        Assertions.assertTrue(html.contains("<form action=\"/messages/1\">"))
        html = client.retrieve(
            HttpRequest.GET<Any>("/frame")
                .accept(MediaType.TEXT_HTML)
                .header(TurboHttpHeaders.TURBO_FRAME, "message_1"), String::class.java
        )
        Assertions.assertFalse(html.contains("<h1>Editing message</h1>"))
        Assertions.assertTrue(html.contains("<turbo-frame id=\"message_1\">"))
        Assertions.assertTrue(html.contains("<form action=\"/messages/1\">"))
    }

    @Requires(property = "spec.name", value = "TurboFrameTest")
    @Controller("/frame")
    internal class TurboFrameController {
        //tag::turboFrameView[]
        @Produces(MediaType.TEXT_HTML)
        @TurboFrameView("form")
        @View("edit")
        @Get
        fun index(): Map<String, Any> {
            return mapOf("message" to Message(1L, "My message title", "My message content"))
        }

        //end::turboFrameView[]
    }

    class Message(val id: Long, val name: String, val content: String)
}
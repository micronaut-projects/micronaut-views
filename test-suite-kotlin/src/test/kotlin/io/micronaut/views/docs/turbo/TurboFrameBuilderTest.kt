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
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.views.ModelAndView
import io.micronaut.views.turbo.TurboFrame
import io.micronaut.views.turbo.http.TurboHttpHeaders
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@Property(name = "spec.name", value = "TurboFrameBuilderTest")
@MicronautTest
class TurboFrameBuilderTest {
    @Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @Test
    fun youCanUseTurboFrameToRenderFrame() {
        val client = httpClient.toBlocking()
        var html = client.retrieve(
            HttpRequest.GET<Any>("/frame/builder").accept(MediaType.TEXT_HTML),
            String::class.java
        )
        Assertions.assertTrue(html.contains("<h1>Editing message</h1>"))
        Assertions.assertTrue(html.contains("<turbo-frame id=\"message_1\">"))
        Assertions.assertTrue(html.contains("<form action=\"/messages/1\">"))
        html = client.retrieve(
            HttpRequest.GET<Any>("/frame/builder")
                .accept(MediaType.TEXT_HTML)
                .header(TurboHttpHeaders.TURBO_FRAME, "message_1"), String::class.java
        )
        Assertions.assertFalse(html.contains("<h1>Editing message</h1>"))
        Assertions.assertTrue(html.contains("<turbo-frame id=\"message_1\">"))
        Assertions.assertTrue(html.contains("<form action=\"/messages/1\">"))
    }

    @Requires(property = "spec.name", value = "TurboFrameBuilderTest")
    @Controller("/frame")
    internal class TurboFrameController {
//tag::turboFrameBuilder[]
        @Produces(MediaType.TEXT_HTML)
        @Get("/builder")
        fun index(@Nullable @Header(TurboHttpHeaders.TURBO_FRAME) turboFrame: String?): HttpResponse<*> {
            val messageId = 1L
            val model = mapOf("message" to Message(messageId,
                "My message title",
                "My message content"))
            return HttpResponse.ok(
                if (turboFrame == null)
                    ModelAndView<Any?>("edit", model)
                else
                    TurboFrame.builder()
                        .id("message_$messageId")
                        .templateModel(model)
                        .templateView("form")
            ).contentType(MediaType.TEXT_HTML)
        }
//end::turboFrameBuilder[]
    }

    class Message(val id: Long, val name: String, val content: String)
}
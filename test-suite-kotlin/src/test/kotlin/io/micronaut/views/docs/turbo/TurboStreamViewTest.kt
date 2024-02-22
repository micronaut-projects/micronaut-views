package io.micronaut.views.docs.turbo

import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.views.turbo.TurboStreamAction
import io.micronaut.views.turbo.TurboStreamView
import io.micronaut.views.turbo.http.TurboHttpHeaders
import io.micronaut.views.turbo.http.TurboMediaType
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import java.io.IOException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@Property(name = "spec.name", value = "TurboViewTest")
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@MicronautTest
class TurboStreamViewTest {

    @Inject
    lateinit var server: EmbeddedServer

    @Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @Test
    @Throws(IOException::class)
    fun turboView() {
        val client = httpClient.toBlocking()

        //tag::turboviewrequest[]
        val request: HttpRequest<*> = HttpRequest.GET<Any>("/turbofruit")
            .accept(TurboMediaType.TURBO_STREAM, MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML)
            .header(TurboHttpHeaders.TURBO_FRAME, "dom_id")
        //end::turboviewrequest[]

        val response = client.exchange(request, String::class.java)
        assertEquals(HttpStatus.OK, response.status())
        assertTrue(response.contentType.isPresent)
        assertEquals(TurboMediaType.TURBO_STREAM, response.contentType.get().toString())
        assertEquals(
            //tag::turboviewresponse[]
            "<turbo-stream action=\"append\" target=\"dom_id\">" +
                "<template>" +
                    "<h1>fruit: Banana</h1>\n" +
                    "<h2>color: Yellow</h2>\n" +
                "</template>" +
            "</turbo-stream>"
            //end::turboviewresponse[]
            , response.body()
        )
    }

    @Requires(property = "spec.name", value = "TurboViewTest")
    @Controller
    class FruitController() {

        //tag::turboview[]
        @Produces(value = [MediaType.TEXT_HTML, TurboMediaType.TURBO_STREAM])
        @TurboStreamView(value = "fruit", action = TurboStreamAction.APPEND)
        @Get("/turbofruit")
        fun show() = mapOf("fruit" to Fruit("Banana", "Yellow"))
        //end::turboview[]
    }
}

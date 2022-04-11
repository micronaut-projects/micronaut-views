package io.micronaut.views.docs.turbo

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.Introspected
import io.micronaut.core.util.CollectionUtils
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.views.turbo.TurboStreamAction
import io.micronaut.views.turbo.TurboView
import io.micronaut.views.turbo.http.TurboHttpHeaders
import io.micronaut.views.turbo.http.TurboMediaType
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.IOException
import java.util.*

@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@Property(name = "spec.name", value = "TurboViewTest")
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@MicronautTest
class TurboViewTest {

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
    .header(TurboHttpHeaders.TURBO_FRAME, "dom_id")
//end::turboviewrequest[]
        val response = client.exchange(request, String::class.java)
        Assertions.assertEquals(HttpStatus.OK, response.status())
        Assertions.assertTrue(response.contentType.isPresent)
        Assertions.assertEquals(TurboMediaType.TURBO_STREAM, response.contentType.get().toString())
        Assertions.assertEquals(
//tag::turboviewresponse[]
"<turbo-stream action=\"append\" target=\"dom_id\">" +
    "<template>" +
        "<h1>fruit: Banana</h1>\n" +
        "<h2>color: Yellow</h2>" +
    "</template>" +
"</turbo-stream>"
//end::turboviewresponse[]
            , response.body()
        )
    }

    @Requires(property = "spec.name", value = "TurboViewTest")
    @Controller
    internal class FruitController() {
//tag::turboview[]
@TurboView(value = "fruit", action = TurboStreamAction.APPEND)
@Get("/turbofruit")
fun show(): Map<String, Any> {
    return Collections.singletonMap("fruit", Fruit("Banana", "Yellow"))
} //end::turboview[]
    }

    @Introspected
    class Fruit(val name: String, val color: String)
}
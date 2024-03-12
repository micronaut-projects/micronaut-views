package io.micronaut.views.docs.htmx

import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.views.ModelAndView
import io.micronaut.views.docs.turbo.Fruit
import io.micronaut.views.htmx.http.HtmxRequestHeaders
import io.micronaut.views.htmx.http.HtmxResponse
import io.micronaut.views.htmx.http.HtmxResponseHeaders
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@Property(name = "spec.name", value = "HtmxRequestHeadersTest")
@MicronautTest
internal class HtmxTest {
    @Test
    fun testHtmxRequestHeaders(@Client("/") httpClient: HttpClient) {
        val client = httpClient.toBlocking()
        var html = client.retrieve(HttpRequest.GET<Any>("/fruits/htmx").header("HX-Request", StringUtils.TRUE))
        assertEquals(
            """
                <h1>fruit: Apple</h1>
                <h2>color: Red</h2>
                
                """.trimIndent(), html
        )
        html = client.retrieve(HttpRequest.GET<Any>("/fruits/htmx"))
        assertTrue(html.contains("<!DOCTYPE html>"))
    }

    @Test
    fun testOutOfBandSwaps(@Client("/") httpClient: HttpClient) {
        val client = httpClient.toBlocking()
        val html = client.retrieve(
            HttpRequest.POST("/fruits/htmx", emptyMap<Any, Any>()).header("HX-Request", StringUtils.TRUE)
        )
        assertEquals(
            """
                <h1>fruit: Apple</h1>
                <h2>color: Red</h2>
                <div id="message" hx-swap-oob="true">Swap me directly!</div>
                """.trimIndent(), html
        )
    }

    @Test
    fun htmxResponseHeaders(@Client("/") httpClient: HttpClient) {
        val client = httpClient.toBlocking()
        val response: HttpResponse<*> = client.exchange<Any, Any>(
            HttpRequest.GET<Any>("/fruits/htmx/responseHeaders").header("HX-Request", StringUtils.TRUE)
        )
        assertEquals(
            StringUtils.TRUE,
            response.headers["HX-Refresh"]
        )
    }

    @Requires(property = "spec.name", value = "HtmxRequestHeadersTest")
    @Controller("/fruits/htmx")
    internal class HtmxRequestHeadersController {
//tag::htmxRequestHeaders[]
        @Get
        fun index(htmxRequestHeaders: HtmxRequestHeaders?): ModelAndView<Map<String, Any>> {
            val model = mapOf("fruit" to Fruit("Apple", "Red"))
            if (htmxRequestHeaders != null) {
                return ModelAndView("fruit", model)
            }
            return ModelAndView("fruits", model)
        }
//end::htmxRequestHeaders[]

//tag::outOfBandSwaps[]
        @Post
        fun outOfBandSwaps(htmxRequestHeaders: HtmxRequestHeaders): HtmxResponse<*> {
            return HtmxResponse.builder<Any>()
                .modelAndView(ModelAndView("fruit", mapOf("fruit" to Fruit("Apple", "Red"))))
                .modelAndView(ModelAndView("swap", emptyMap<Any, Any>()))
                .build()
        }
//end::outOfBandSwaps[]

//tag::htmxResponseHeaders[]
        @Get("/responseHeaders")
        fun htmxResponseHeaders(htmxRequestHeaders: @NonNull HtmxRequestHeaders): HttpResponse<*> {
            return HttpResponse.ok<Any>().header(HtmxResponseHeaders.HX_REFRESH, StringUtils.TRUE)
        }
//end::htmxResponseHeaders[]


    }
}
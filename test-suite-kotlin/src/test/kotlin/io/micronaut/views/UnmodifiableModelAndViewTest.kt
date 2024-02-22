package io.micronaut.views

import io.micronaut.context.ApplicationContext
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.views.model.UnmodifiableFruitsController
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// Issue: https://github.com/micronaut-projects/micronaut-views/issues/336
class UnmodifiableModelAndViewTest {

    @Test
    fun anUnmodifiableViewModelReportsErrorButDoesNotCrash() {
        ApplicationContext.run(
            EmbeddedServer::class.java, mapOf(
                "spec.name" to "UnmodifiableModelAndViewSpec",
                "micronaut.views.soy.enabled" to StringUtils.FALSE
            )
        ).use { embeddedServer ->
            HttpClient.create(embeddedServer.url).use { httpClient ->
                //given:
                val client = httpClient.toBlocking()

                //expect:
                assertTrue(embeddedServer.applicationContext.containsBean(UnmodifiableFruitsController::class.java))

                //when:
                val request = HttpRequest.GET<Any>("/unmodifiable").basicAuth("john", "secret")
                val response = client.exchange(request, String::class.java)

                //then:
                assertEquals(HttpStatus.OK, response.status())

                //when:
                val html = response.body()

                //then:
                assertNotNull(html)
                assertTrue(html.contains("<blink>Security was added</blink>"))
                assertTrue(html.contains("<h1>fruit: plum</h1>"))
                assertTrue(html.contains("<h1>color: plum</h1>"))
            }
        }
    }

    @Test
    fun aModifiableViewModelStillAddsSecurity() {
        ApplicationContext.run(
            EmbeddedServer::class.java, mapOf(
                "spec.name" to "UnmodifiableModelAndViewSpec",
                "micronaut.views.soy.enabled" to StringUtils.FALSE
            )
        ).use { embeddedServer ->
            HttpClient.create(embeddedServer.url).use { httpClient ->
                //given:
                val client = httpClient.toBlocking()
                //expect:
                assertTrue(embeddedServer.applicationContext.containsBean(UnmodifiableFruitsController::class.java))

                //when:
                val request = HttpRequest.GET<Any>("/modifiable").basicAuth("john", "secret")
                val response = client.exchange(request, String::class.java)

                //then:
                assertEquals(HttpStatus.OK, response.status())

                //when:
                val html = response.body()

                //then:
                assertNotNull(html)
                assertTrue(html.contains("<blink>Security was added</blink>"))
                assertTrue(html.contains("<h1>fruit: plum</h1>"))
                assertTrue(html.contains("<h1>color: plum</h1>"))
            }
        }
    }
}

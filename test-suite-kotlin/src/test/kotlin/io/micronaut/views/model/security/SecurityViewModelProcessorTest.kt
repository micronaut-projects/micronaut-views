package io.micronaut.views.model.security

import io.micronaut.context.ApplicationContext
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SecurityViewModelProcessorTest {

    @Test
    fun ifMicronautSecurityViewsModelDecoratorEnabledTrueSecurityViewsModelDecoratorBeanExists() {
        //given:
        ApplicationContext.run(mapOf(
                "micronaut.views.soy.enabled" to StringUtils.FALSE,
                "micronaut.security.views-model-decorator.enabled" to StringUtils.FALSE
            )
        ).use { applicationContext ->
            assertFalse(applicationContext.containsBean(SecurityViewModelProcessor::class.java))
        }
    }

    @Test
    fun byDefaultSecurityViewsModelDecoratorBeanExists() {
        //given:
        ApplicationContext.run(mapOf(
                "micronaut.views.soy.enabled" to StringUtils.FALSE
            )
        ).use { applicationContext ->
            assertTrue(applicationContext.containsBean(SecurityViewModelProcessor::class.java))
        }
    }

    @Test
    fun aCustomSecurityPropertyNameCanBeInjectedToTheModel() {
        //given:
        ApplicationContext.run(
            EmbeddedServer::class.java, mapOf(
                "spec.name" to "SecurityViewModelProcessorSpec",
                "micronaut.views.soy.enabled" to StringUtils.FALSE,
                "micronaut.security.views-model-decorator.security-key" to "securitycustom"
            )
        ).use { embeddedServer ->
            HttpClient.create(embeddedServer.url).use { httpClient ->
                //expect:
                assertTrue(embeddedServer.applicationContext.containsBean(BooksController::class.java))

                //and:
                assertTrue(embeddedServer.applicationContext.containsBean(MockAuthenticationProvider::class.java))

                //and:
                assertTrue(embeddedServer.applicationContext.containsBean(SecurityViewModelProcessor::class.java))

                //when:
                val request = HttpRequest.GET<Any>("/").basicAuth("john", "secret")
                val response = httpClient.toBlocking().exchange(request, String::class.java)

                //then:
                assertEquals(HttpStatus.OK, response.status())

                //when:
                val html = response.body()

                //then:
                assertNotNull(html)

                //and:
                assertFalse(html.contains("User: john"))

                //and:
                assertTrue(html.contains("Custom: john"))
            }
        }
    }

    @Test
    fun securityPropertyIsInjectedToTheModel() {
        //given:
        ApplicationContext.run(
            EmbeddedServer::class.java, mapOf(
                "spec.name" to "SecurityViewModelProcessorSpec",
                "micronaut.views.soy.enabled" to StringUtils.FALSE
            )
        ).use { embeddedServer ->
            HttpClient.create(embeddedServer.url).use { httpClient ->
                //expect:
                assertTrue(embeddedServer.applicationContext.containsBean(io.micronaut.views.model.security.BooksController::class.java))

                //and:
                assertTrue(embeddedServer.applicationContext.containsBean(MockAuthenticationProvider::class.java))

                //and:
                assertTrue(embeddedServer.applicationContext.containsBean(SecurityViewModelProcessor::class.java))

                //when:
                val request = HttpRequest.GET<Any>("/").basicAuth("john", "secret")
                val response = httpClient.toBlocking().exchange(request, String::class.java)

                //then:
                assertEquals(HttpStatus.OK, response.status())

                //when:
                val html = response.body()

                //then:
                assertNotNull(html)
                assertTrue(html.contains("User: john email: john@email.com"))

                //and:
                assertTrue(html.contains("Developing Microservices"))
            }
        }
    }
}

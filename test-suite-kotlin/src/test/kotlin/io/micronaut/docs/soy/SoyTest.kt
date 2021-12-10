package io.micronaut.docs.soy

import io.micronaut.context.annotation.Property
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@Property(name = "spec.name", value = "soy")
@Property(name = "micronaut.views.velocity.enabled", value = StringUtils.FALSE)
@MicronautTest
internal class SoyTest {
    @Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @Test
    fun invokingSoyRendersSoyTemplateFromAControllerReturningAmap() {
        //when:
        val rsp = httpClient.toBlocking().exchange(
            "/soy",
            String::class.java
        )

        //then:
        Assertions.assertEquals(HttpStatus.OK, rsp.status())

        //when:
        val body = rsp.body()

        //then:
        Assertions.assertNotNull(body)
        Assertions.assertTrue(body!!.contains("<h1>username: <span>sgammon</span></h1>"))
    }
}
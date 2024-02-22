package io.micronaut.views.docs.soy

import io.micronaut.context.annotation.Property
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@Property(name = "spec.name", value = "AsyncSpec")
@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@MicronautTest
class AsyncTest  {
    @Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    //@Issue("https://github.com/micronaut-projects/micronaut-views/issues/68")
    @Test
    fun verifyAControllerWhichReturnsAModelWithAnAsyncCoroutineWorks(){
        //when:
        val request: HttpRequest<Any> = HttpRequest.GET("async")
        val html = httpClient.toBlocking().retrieve(request)

        //then:
        Assertions.assertTrue(html.contains("hello, world"));
    }
}

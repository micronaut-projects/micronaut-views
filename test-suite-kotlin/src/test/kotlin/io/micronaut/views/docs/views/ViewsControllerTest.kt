package io.micronaut.views.docs.views

import io.micronaut.context.annotation.Property
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@Property(name = "spec.name", value = "ViewsControllerTest")
@MicronautTest
internal class ViewsControllerTest {

    @Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @Test
    fun aViewCanBeRenderedFromAMapAPojoOrAModelAndView() {
        val client = httpClient.toBlocking()
        for (path in listOf("/views", "/views/pojo", "/views/modelAndView")) {
            val html = client.retrieve(HttpRequest.GET<Any>(path), String::class.java)
            assertTrue(html.contains("<h1>username: <span>sdelamo</span></h1>"), path)
        }
    }
}

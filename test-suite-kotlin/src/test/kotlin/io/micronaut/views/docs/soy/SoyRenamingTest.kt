package io.micronaut.views.docs.soy

import io.micronaut.context.annotation.Property
import io.micronaut.core.util.StringUtils
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@Property(name = "spec.name", value = "soy")
@Property(name = "micronaut.views.soy.renaming-enabled", value = StringUtils.TRUE)
@Property(name = "micronaut.views.velocity.enabled", value = StringUtils.FALSE)
@MicronautTest
internal class SoyRenamingTest {
    @Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @Test
    fun cssClassesAreRenamedWithTheRenamingMap() {
        val html = httpClient.toBlocking().retrieve("/soy/renaming")
        assertTrue(html.contains("<div class=\"a-b-c a-b-c-d\">Renamed</div>"), html)
    }
}

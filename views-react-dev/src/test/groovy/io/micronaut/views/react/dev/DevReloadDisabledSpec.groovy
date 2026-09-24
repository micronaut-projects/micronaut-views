package io.micronaut.views.react.dev

import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

/**
 * In dev, but not asked for. Being on the classpath is not consent.
 */
@MicronautTest(environments = ["dev"], rebuildContext = true)
@Property(name = "spec.name", value = "devreload")
class DevReloadDisabledSpec extends Specification {
    @Inject
    @Client("/")
    HttpClient client

    void "nothing is injected unless enabled"() {
        when:
        String body = client.toBlocking().retrieve(HttpRequest.GET("/page").accept(MediaType.TEXT_HTML))

        then:
        body.contains("Hello there")
        !body.contains("data-micronaut-views-react-dev-reload")
    }
}

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
 * The script goes into rendered HTML and nowhere else.
 */
@MicronautTest(environments = ["dev"], rebuildContext = true)
@Property(name = "spec.name", value = "devreload")
@Property(name = "micronaut.views.react.dev.enabled", value = "true")
class DevReloadScriptInjectionSpec extends Specification {
    @Inject
    @Client("/")
    HttpClient client

    void "an HTML response carries the refresh script"() {
        when:
        String body = client.toBlocking().retrieve(HttpRequest.GET("/page").accept(MediaType.TEXT_HTML))

        then: "the page is intact"
        body.contains("Hello there")

        and: "and listens on the configured endpoint"
        body.contains("data-micronaut-views-react-dev-reload")
        body.contains("new EventSource('/micronaut/views/react/dev-reload')")
        body.contains("location.reload()")
    }

    void "a response that is not HTML is left alone"() {
        when:
        String body = client.toBlocking().retrieve(HttpRequest.GET("/page/data").accept(MediaType.APPLICATION_JSON))

        then:
        body.contains("there")
        !body.contains("data-micronaut-views-react-dev-reload")
    }
}

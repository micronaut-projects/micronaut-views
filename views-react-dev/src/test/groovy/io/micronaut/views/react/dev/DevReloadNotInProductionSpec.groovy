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
 * Outside the development environment none of this exists, even when the property asks for it.
 * The property alone is not enough: a setting left in a committed config file must not turn a
 * production deployment into one that serves an unauthenticated endpoint.
 */
@MicronautTest(environments = ["prod"], rebuildContext = true)
@Property(name = "spec.name", value = "devreload")
@Property(name = "micronaut.views.react.dev.enabled", value = "true")
class DevReloadNotInProductionSpec extends Specification {
    @Inject
    @Client("/")
    HttpClient client

    void "the script is not injected outside dev"() {
        when:
        String body = client.toBlocking().retrieve(HttpRequest.GET("/page").accept(MediaType.TEXT_HTML))

        then:
        body.contains("Hello there")
        !body.contains("data-micronaut-views-react-dev-reload")
    }
}

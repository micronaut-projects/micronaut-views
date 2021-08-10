package io.micronaut.doc.soy

import io.micronaut.context.annotation.Property
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@Property(name = "spec.name", value = "AsyncSpec")
@Property(name = 'micronaut.views.soy.enabled', value = StringUtils.FALSE)
@MicronautTest
class AsyncSpec extends Specification {
    @Inject
    @Client("/")
    HttpClient client

    void "verify a controller which returns a model with an async coroutine works"() {
        when:
        HttpRequest request = HttpRequest.GET("/async")
        String html = client.toBlocking().retrieve(request)

        then:
        html.contains("hello, world")
    }
}

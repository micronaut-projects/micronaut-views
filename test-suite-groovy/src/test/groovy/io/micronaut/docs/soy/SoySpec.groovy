package io.micronaut.docs.soy

import io.micronaut.context.annotation.Property
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.Specification

import jakarta.inject.Inject

@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@Property(name = "spec.name", value = "soy")
@Property(name = "micronaut.views.velocity.enabled", value = StringUtils.FALSE)
@MicronautTest
class SoySpec extends Specification {
    @Inject
    @Client("/")
    public HttpClient httpClient;

    def "invoking /soy renders soy template from a controller returning a map"() {
        when:
        HttpResponse<String> rsp = httpClient.toBlocking().exchange('/soy', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("<h1>username: <span>sgammon</span></h1>")
    }
}

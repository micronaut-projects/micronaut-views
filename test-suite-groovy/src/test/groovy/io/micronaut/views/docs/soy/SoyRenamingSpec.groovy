package io.micronaut.views.docs.soy

import io.micronaut.context.annotation.Property
import io.micronaut.core.util.StringUtils
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@Property(name = "spec.name", value = "soy")
@Property(name = "micronaut.views.soy.renaming-enabled", value = StringUtils.TRUE)
@Property(name = "micronaut.views.velocity.enabled", value = StringUtils.FALSE)
@MicronautTest
class SoyRenamingSpec extends Specification {
    @Inject
    @Client("/")
    HttpClient httpClient

    void "css classes are renamed with the renaming map"() {
        when:
        String html = httpClient.toBlocking().retrieve("/soy/renaming")

        then:
        html.contains('<div class="a-b-c a-b-c-d">Renamed</div>')
    }
}

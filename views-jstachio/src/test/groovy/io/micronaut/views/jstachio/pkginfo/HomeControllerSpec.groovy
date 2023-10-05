package io.micronaut.views.jstachio.pkginfo

import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject;
import spock.lang.Specification;

@Property(name = "spec.name", value = "HomeControllerSpec")
@MicronautTest
class HomeControllerSpec extends Specification {
    @Inject
    @Client("/")
    HttpClient httpClient
    void "jstachio can use a JStacheConfig in package-info.java"() {
        given:
        BlockingHttpClient client = httpClient.toBlocking()

        when:
        String html = client.retrieve(HttpRequest.GET("/views"), String)

        then:
        html.contains("<h1>username: <span>sdelamo</span></h1>")
    }
}

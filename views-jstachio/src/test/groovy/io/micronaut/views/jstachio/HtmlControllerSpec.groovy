
package io.micronaut.views.jstachio

import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.PendingFeature
import spock.lang.Specification

@Property(name = "spec.name", value = "HtmlControllerSpec")
@MicronautTest
class HtmlControllerSpec extends Specification {
    @Inject
    @Client("/")
    HttpClient httpClient

    void "it is possible to return html not handled by JStachioMessageBodyWriter"() {
        given:
        BlockingHttpClient client = httpClient.toBlocking()

        when:
        String html = client.retrieve(HttpRequest.GET("/html"), String)

        then:
        "<!DOCTYPE html><html><head></head><body><h1>Sergio</h1></body></html>" == html
    }

    @PendingFeature(reason = "https://github.com/micronaut-projects/micronaut-core/pull/9789")
    void "it is possible to return html without view and not handled by JStachioMessageBodyWriter"() {
        given:
        BlockingHttpClient client = httpClient.toBlocking()

        when:
        String html = client.retrieve(HttpRequest.GET("/html/noview"), String)

        then:
        "<!DOCTYPE html><html><head></head><body><h1>Sergio</h1></body></html>" == html
    }
}

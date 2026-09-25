package io.micronaut.views.react.dev

import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

/**
 * Without this the refresh is pointless: a rendered page carries no cache headers and no validator,
 * so the browser is free to answer its own {@code location.reload()} from cache. Measured in a real
 * browser before this existed -- it reloaded, was served the document it already had, and stopped.
 */
@MicronautTest(environments = ["dev"], rebuildContext = true)
@Property(name = "spec.name", value = "devreload-nostore")
@Property(name = "micronaut.views.react.dev.enabled", value = "true")
class DevReloadNoStoreSpec extends Specification {
    @Inject
    @Client("/")
    HttpClient client

    void "a response is marked no-store while dev reload is on"() {
        when:
        HttpResponse<String> response = client.toBlocking()
            .exchange(HttpRequest.GET("/nostore").accept(MediaType.TEXT_HTML), String)

        then:
        response.body().contains("a page")
        response.header(HttpHeaders.CACHE_CONTROL) == "no-store, must-revalidate"
    }

    @Requires(property = "spec.name", value = "devreload-nostore")
    @Controller("/nostore")
    static class TestPage {
        @Get
        @Produces(MediaType.TEXT_HTML)
        String page() {
            return "<html><body>a page</body></html>"
        }
    }
}

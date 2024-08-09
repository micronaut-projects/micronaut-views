package io.micronaut.views.react

import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpRequest
import io.micronaut.core.io.Writable
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification
/**
 * Preact is an alternative implementation of React that focuses on being smaller and having fewer sharp edges in
 * the API at a cost in features. We verify here that we can handle Preact apps without the compatability API they
 * provide.
 */
@MicronautTest(startApplication = false)
@Property(name = "micronaut.views.react.client-bundle-url", value = "/static/client.preact.js")
@Property(name = "micronaut.views.react.server-bundle-path", value = "classpath:views/ssr-components.preact.mjs")
@Property(name = "micronaut.views.react.render-script", value = "classpath:/io/micronaut/views/react/preact.js")
class PreactViewRenderSpec extends Specification {
    @Inject
    ReactViewsRenderer<?> renderer

    void "views can be rendered with basic props and no request"() {
        when:
        Writable writable = renderer.render("App", TestProps.basic, null)
        String result = new StringWriter().with {
            writable.writeTo(it)
            it.toString()
        }

        then:
        result.contains("/static/client.preact.js")
        result.contains("Hello there")  // static HTML
        result.contains("\"name\":\"Mike\"")  // props
    }

    void "views can be rendered with basic props with a request"() {
        given:
        HttpRequest<?> req = Mock()
        req.getUri() >> URI.create("https://localhost/demopage")

        when:
        Writable writable = renderer.render("App", TestProps.basic, req)
        String result = new StringWriter().with {
            writable.writeTo(it)
            it.toString()
        }

        then:
        result.contains("/static/client.preact.js")
        result.contains("\"name\":\"Mike\"")  // props
        result.contains("URL is https://localhost/demopage")
    }
}

package io.micronaut.views.react

import io.micronaut.context.annotation.Property
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
@Property(name = "micronaut.views.folder", value = "src/test/resources/views")
@Property(name = "micronaut.views.react.clientBundleURL", value = "/static/client.preact.js")
@Property(name = "micronaut.views.react.serverBundlePath", value = "ssr-components.preact.mjs")
class PreactViewRenderSpec extends Specification {
    @Inject
    ReactViewsRenderer<?, ?> renderer

    void "views can be rendered with basic props"() {
        when:
        Writable writable = renderer.render("App", ["name": "Mike"], null)
        String result = new StringWriter().with {
            writable.writeTo(it)
            it.toString()
        }

        then:
        result.contains("/static/client.preact.js")
        result.contains("Hello there")  // static HTML
        result.contains("\"name\":\"Mike\"")  // props
    }
    // TODO: tests for server prefetch.
}

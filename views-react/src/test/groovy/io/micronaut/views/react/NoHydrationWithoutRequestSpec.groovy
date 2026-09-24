package io.micronaut.views.react

import io.micronaut.context.annotation.Property
import io.micronaut.core.io.Writable
import io.micronaut.http.HttpRequest
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

/**
 * A render with no request is not going to a browser -- an email body is the common case -- so with
 * hydrate-without-request off it must carry markup only. The whole view model would otherwise be
 * serialised into the message.
 */
@MicronautTest(startApplication = false, rebuildContext = true)
@Property(name = "micronaut.views.react.server-bundle-path", value = "classpath:views/ssr-components.mjs")
@Property(name = "micronaut.views.react.hydrate-without-request", value = "false")
class NoHydrationWithoutRequestSpec extends Specification {
    @Inject
    ReactViewsRenderer<?> renderer

    void "a render without a request carries no hydration bootstrap"() {
        when:
        Writable writable = renderer.render("App", TestProps.basic, null)
        String result = WritableUtils.writableToString(writable).orElseThrow()

        then: "the markup is there"
        result.contains("Hello there")
        result.contains("Reading a property works: <!-- -->foo")

        and: "the model and the client bundle are not"
        !result.contains("var Micronaut =")
        !result.contains("rootProps")
        !result.contains("/static/client.js")
        !result.contains("\"innerBean\"")
    }

    void "a render with a request still hydrates"() {
        given:
        HttpRequest<?> req = Mock()
        req.getUri() >> URI.create("https://localhost/demopage")

        when:
        Writable writable = renderer.render("App", TestProps.basic, req)
        String result = WritableUtils.writableToString(writable).orElseThrow()

        then:
        result.contains("/static/client.js")
        result.contains("\"name\":\"Mike\"")
    }
}

package io.micronaut.views.react

import io.micronaut.context.annotation.Property
import io.micronaut.core.io.Writable
import io.micronaut.http.HttpRequest
import io.micronaut.http.exceptions.MessageBodyException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import reactor.core.publisher.Mono
import spock.lang.Specification

@MicronautTest(startApplication = false, rebuildContext = true)
@Property(name = "micronaut.views.react.server-bundle-path", value = "classpath:views/ssr-components.mjs")
class ReactViewRenderSpec extends Specification {
    @Inject
    ReactViewsRenderer<?> renderer;

    void "views can be rendered with basic props"() {
        when:
<<<<<<< HEAD
        Writable writable = Mono.from(renderer.render("App", TestProps.basic, null)).block()
=======
        Writable writable = renderer.render("App", TestProps.basic, null)
>>>>>>> 5.7.x
        String result = WritableUtils.writableToString(writable).orElseThrow()

        then:
        result.contains("Hello there")
        result.contains("Reading a property works: <!-- -->foo")
        result.contains("Reading a null works: </p>")
        result.contains("\"name\":\"Mike\"")
        result.contains("\"innerBean\":{\"a\":10,\"list\":[\"one\",\"two\"],\"map\":{}}")
        result.contains("Calling a method works: <!-- -->Goodbye Bob!")
    }

    void "views can be rendered with basic props with a request"() {
        given:
        HttpRequest<?> req = Mock()
        req.getUri() >> URI.create("https://localhost/demopage")

        when:
<<<<<<< HEAD
        Writable writable = Mono.from(renderer.render("App", TestProps.basic, req)).block()
=======
        Writable writable = renderer.render("App", TestProps.basic, req)
>>>>>>> 5.7.x
        String result = WritableUtils.writableToString(writable).orElseThrow()

        then:
        result.contains("/static/client.js")
        result.contains("\"name\":\"Mike\"")  // props
        result.contains("URL is <!-- -->https://localhost/demopage")
    }

    void "host access is OK if sandbox is disabled"() {
        when:
        Mono.from(renderer.render("App", TestProps.triggerSandbox, null)).block().writeTo(OutputStream.nullOutputStream())

        then:
        notThrown(MessageBodyException)
    }
}

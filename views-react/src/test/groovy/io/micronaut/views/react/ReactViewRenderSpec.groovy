package io.micronaut.views.react

import io.micronaut.context.annotation.Property
import io.micronaut.core.io.Writable
import io.micronaut.http.HttpRequest
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest(startApplication = false, rebuildContext = true)
@Property(name = "micronaut.views.folder", value = "src/test/resources/views")
class ReactViewRenderSpec extends Specification {
    @Inject
    ReactViewsRenderer<?> renderer;

    void "views can be rendered with basic props"() {
        when:
        Writable writable = renderer.render("App", ["name": "Mike"], null)
        String result = new StringWriter().with {
            writable.writeTo(it)
            it.toString()
        }

        then:
        result.contains("Hello there")
        result.contains("\"name\":\"Mike\"")
    }

    void "views can be rendered with basic props with a request"() {
        given:
        HttpRequest<?> req = Mock()
        req.getUri() >> URI.create("https://localhost/demopage")

        when:
        Writable writable = renderer.render("App", ["name": "Mike"], req)
        String result = new StringWriter().with {
            writable.writeTo(it)
            it.toString()
        }

        then:
        result.contains("/static/client.js")
        result.contains("\"name\":\"Mike\"")  // props
        result.contains("URL is <!-- -->https://localhost/demopage")
    }
}

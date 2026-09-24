package io.micronaut.views.react

import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.core.io.Writable
import io.micronaut.http.HttpRequest
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import spock.lang.Specification

import java.io.Writer

/**
 * The hook a development module uses to add to every rendered page.
 *
 * <p>There is no HTTP filter equivalent, which is why this exists: a {@code @View} route's response
 * body is still a ModelAndView while filters run, and the markup is not produced until a
 * MessageBodyWriter encodes it -- after the last filter. This is where rendered markup exists.
 */
@MicronautTest(startApplication = false, rebuildContext = true)
@Property(name = "spec.name", value = "postprocessor")
@Property(name = "micronaut.views.react.server-bundle-path", value = "classpath:views/ssr-components.mjs")
class RenderPostProcessorSpec extends Specification {
    @Inject
    ReactViewsRenderer<?> renderer

    void "a post processor is appended to a page rendered for a browser"() {
        given:
        HttpRequest<?> req = Mock()
        req.getUri() >> URI.create("https://localhost/demopage")

        when:
        Writable writable = renderer.render("App", TestProps.basic, req)
        String result = WritableUtils.writableToString(writable).orElseThrow()

        then: "the component's markup is there, and the addition comes after it"
        result.contains("Hello there")
        result.contains("<!--appended-by-post-processor-->")
        result.indexOf("Hello there") < result.indexOf("<!--appended-by-post-processor-->")
    }

    void "a post processor sees a render with no request, and can decline it"() {
        when: "an email body, which no browser will receive"
        Writable writable = renderer.render("App", TestProps.basic, null)
        String result = WritableUtils.writableToString(writable).orElseThrow()

        then:
        result.contains("Hello there")
        !result.contains("<!--appended-by-post-processor-->")
    }

    @Requires(property = "spec.name", value = "postprocessor")
    @Singleton
    static class TestPostProcessor implements ReactRenderPostProcessor {
        @Override
        void afterRender(Writer writer, HttpRequest<?> request) {
            if (request != null) {
                writer.write("<!--appended-by-post-processor-->")
            }
        }
    }
}

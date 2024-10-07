package io.micronaut.views.react

import io.micronaut.context.annotation.Property
import io.micronaut.core.io.Writable
import io.micronaut.http.exceptions.MessageBodyException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import org.graalvm.polyglot.PolyglotException
import spock.lang.Specification

@MicronautTest(startApplication = false, rebuildContext = true)
@Property(name = "micronaut.views.react.server-bundle-path", value = "classpath:views/ssr-components.mjs")
@Property(name = "micronaut.views.react.sandbox", value = "true")
class SandboxReactRenderSpec extends Specification {
    @Inject
    ReactViewsRenderer<?> renderer

    void "views can be rendered with sandboxing enabled"() {
        when:
        Writable writable = renderer.render("App", TestProps.basic, null)
        String result = new StringWriter().with {
            writable.writeTo(it)
            it.toString()
        }

        then:
        result.contains("\"name\":\"Mike\"")
        result.contains("\"innerBean\":{\"a\":10,\"list\":[\"one\",\"two\"],\"map\":{}}")
        result.contains("Calling a method works: <!-- -->Goodbye Bob!")
    }

    void "host types are inaccessible with the sandbox enabled"() {
        when:
        Writable writable = renderer.render("App", TestProps.triggerSandbox, null)
        new StringWriter().with {
            writable.writeTo(it)
            it.toString()
        }

        then:
        def t = thrown(MessageBodyException)
        t.cause instanceof PolyglotException
        t.cause.message.contains("Java is not defined")
    }
}

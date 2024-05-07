package io.micronaut.views.react

import io.micronaut.context.annotation.Property
import io.micronaut.context.exceptions.BeanInstantiationException
import io.micronaut.core.io.Writable
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.FailsWith
import spock.lang.Specification

@MicronautTest(startApplication = false, rebuildContext = true)
@Property(name = "micronaut.views.folder", value = "src/test/resources/views")
@Property(name = "micronaut.views.react.sandbox", value = "true")
class SandboxReactRenderSpec extends Specification {
    @Inject
    ReactViewsRenderer<?, ?> renderer;

    // The version of GraalJS currently depended on is not compatible with the sandbox. When GraalJS is upgraded,
    // this unit test can be enabled.
    @FailsWith(BeanInstantiationException)
    void "views can be rendered with sandboxing enabled"() {
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

    void "host types are inaccessible with the sandbox enabled"() {
        when:
        Writable writable = renderer.render("App", ["name": "Mike", "triggerSandbox": true], null)
        new StringWriter().with {
            writable.writeTo(it)
            it.toString()
        }

        then:
        // The version of GraalJS currently depended on is not compatible with the sandbox. When GraalJS is upgraded,
        // this unit test can be enabled.
        thrown(BeanInstantiationException)
        //thrown(PolyglotException)
    }
}

package io.micronaut.views.thymeleaf

import io.micronaut.core.io.Writable
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest(startApplication = false)
class ThymeleafViewRenderFragment extends Specification {

    @Inject
    ThymeleafViewsRenderer<?> viewRenderer

    void "can render fragment"() {
        when:
        Writable writeable = viewRenderer.render("fragment :: thefragment", ["some": "data"], null)
        String result = new StringWriter().with {
            writeable.writeTo(it)
            it.toString()
        }

        then:
        result == "<div>FRAGMENT</div>"
    }

    void "can render main body"() {
        when:
        Writable writeable = viewRenderer.render("fragment", ["some": "data"], null)
        String result = new StringWriter().with {
            writeable.writeTo(it)
            it.toString()
        }

        then:
        result.contains("MAIN") && result.contains("FRAGMENT")
    }
}

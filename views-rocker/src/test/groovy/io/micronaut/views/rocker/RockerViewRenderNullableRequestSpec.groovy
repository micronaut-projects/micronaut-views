package io.micronaut.views.rocker

import io.micronaut.core.io.Writable
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.views.ViewsRenderer
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest(startApplication = false)
class RockerViewRenderNullableRequestSpec extends Specification {

    @Inject
    RockerViewsRenderer<?, ?> viewRenderer

    void "views can be render with no request"() {
        when:
        Writable writeable = viewRenderer.render("tim", ["username": "Tim"], null)
        String result = new StringWriter().with {
            writeable.writeTo(it)
            it.toString()
        }

        then:
        result.contains("username: <span>Tim</span>")
    }
}

package io.micronaut.views.freemarker

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification
import io.micronaut.core.io.Writable

@MicronautTest(startApplication = false)
class FreemarkerViewRenderNullableRequestSpec extends Specification {

    @Inject
    FreemarkerViewsRenderer<?, ?> viewRenderer

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

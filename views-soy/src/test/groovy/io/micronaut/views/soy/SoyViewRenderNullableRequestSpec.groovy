package io.micronaut.views.soy

import io.micronaut.context.annotation.Property
import io.micronaut.core.io.Writable
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest(startApplication = false)
@Property(name = "spec.name", value = "soy")
class SoyViewRenderNullableRequestSpec extends Specification {

    @Inject
    SoySauceViewsRenderer<?> viewRenderer

    void "views can be render with no request"() {
        when:
        Writable writeable = viewRenderer.render("sample.tim", ["username": "Tim"], null)
        String result = new StringWriter().with {
            writeable.writeTo(it)
            it.toString()
        }

        then:
        result.contains("username: <span>Tim</span>")
    }
}

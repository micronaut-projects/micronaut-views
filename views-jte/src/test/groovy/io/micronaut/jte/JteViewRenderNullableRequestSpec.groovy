package io.micronaut.jte

import io.micronaut.core.io.Writable
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.views.ViewsRenderer
import io.micronaut.views.jte.HtmlJteViewsRenderer
import io.micronaut.views.jte.JteViewsRenderer
import io.micronaut.views.jte.PlainJteViewsRenderer
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest(startApplication = false)
class JteViewRenderNullableRequestSpec extends Specification {

    @Inject
    PlainJteViewsRenderer<?> plainJteViewsRenderer

    @Inject
    HtmlJteViewsRenderer<?> htmlJteViewsRenderer

    void "views can be render with no request"() {
        expect:
        output(plainJteViewsRenderer).contains("username: <span>Tim</span>")
        output(htmlJteViewsRenderer).contains("username: <span>Tim</span>")
    }

    private static String output(ViewsRenderer<?, ?> viewsRenderer) {
        Writable writeable = viewsRenderer.render("tim", ["username": "Tim"], null)
        new StringWriter().with {
            writeable.writeTo(it)
            it.toString()
        }
    }
}

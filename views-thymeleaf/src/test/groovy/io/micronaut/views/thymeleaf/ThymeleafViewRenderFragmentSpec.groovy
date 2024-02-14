package io.micronaut.views.thymeleaf

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

import static io.micronaut.views.thymeleaf.WriteableUtils.*

@MicronautTest(startApplication = false)
class ThymeleafViewRenderFragmentSpec extends Specification {

    @Inject
    ThymeleafViewsRenderer<?> viewRenderer

    void "can render fragment"() {
        expect:
        "<div>FRAGMENT</div>" == writableToString(viewRenderer.render("fragment :: thefragment", ["some": "data"], null))

        and:
        "<div>FRAGMENT 2</div>" == writableToString(viewRenderer.render("fragment :: thefragment2", ["some": "data"], null))
    }

    void "can render main body"() {
        when:
        String result = writableToString(viewRenderer.render("fragment", ["some": "data"], null))

        then:
        result.contains("MAIN") && result.contains("FRAGMENT")
    }

    void "exists is successful when using fragments"() {
        expect:
        viewRenderer.exists("fragment :: thefragment")
    }

    void "exists is successful when using regular view name"() {
        expect:
        viewRenderer.exists("fragment")
    }
}

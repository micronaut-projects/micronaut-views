package io.micronaut.views.react.dev

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpRequest
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.views.react.ReactRenderPostProcessor
import jakarta.inject.Inject
import spock.lang.Specification

/**
 * What gets added to a rendered page, and to what.
 */
@MicronautTest(startApplication = false, environments = ["dev"], rebuildContext = true)
@Property(name = "micronaut.views.react.dev.enabled", value = "true")
class DevReloadScriptInjectionSpec extends Specification {
    @Inject
    ApplicationContext context

    void "a render for a browser gets the refresh script"() {
        given:
        ReactRenderPostProcessor injector = context.getBean(ReactRenderPostProcessor)
        HttpRequest<?> request = Mock()
        def writer = new StringWriter()

        when:
        injector.afterRender(writer, request)

        then:
        writer.toString().contains("data-micronaut-views-react-dev-reload")
        writer.toString().contains("new EventSource('/micronaut/views/react/dev-reload')")
        writer.toString().contains("location.reload()")
    }

    void "a render with no request gets nothing"() {
        given: "an email body, which no browser will receive"
        ReactRenderPostProcessor injector = context.getBean(ReactRenderPostProcessor)
        def writer = new StringWriter()

        when:
        injector.afterRender(writer, null)

        then:
        writer.toString().isEmpty()
    }

    void "the endpoint the script listens on follows the configured path"() {
        expect:
        context.getBean(ReactDevConfiguration).path == "/micronaut/views/react/dev-reload"
    }
}

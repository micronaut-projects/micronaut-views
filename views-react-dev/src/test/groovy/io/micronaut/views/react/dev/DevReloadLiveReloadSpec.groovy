package io.micronaut.views.react.dev

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.views.react.ReactBrowserRefresh
import io.micronaut.views.react.ReactRenderPostProcessor
import jakarta.inject.Singleton
import spock.lang.AutoCleanup
import spock.lang.Specification

/**
 * Under the development launcher, with its LiveReload server running, the page is refreshed through LiveReload and
 * the server-sent events channel stays out of it; it is the fallback otherwise.
 */
class DevReloadLiveReloadSpec extends Specification {
    @AutoCleanup
    ApplicationContext context = ApplicationContext.run(["micronaut.views.react.dev.enabled": true, "spec.name": "DevReloadLiveReloadSpec"], "dev")

    TestBrowserRefresh refresh = context.getBean(TestBrowserRefresh)

    void "with LiveReload active a rendered page gets no script of its own"() {
        given:
        refresh.active = true
        def writer = new StringWriter()

        when:
        context.getBean(ReactRenderPostProcessor).afterRender(writer, Mock(HttpRequest))

        then:
        writer.toString().isEmpty()
    }

    void "with the LiveReload server off the page listens over server-sent events"() {
        given:
        refresh.active = false
        def writer = new StringWriter()

        when:
        context.getBean(ReactRenderPostProcessor).afterRender(writer, Mock(HttpRequest))

        then:
        writer.toString().contains("new EventSource('/micronaut/views/react/dev-reload')")
    }

    @Singleton
    @Requires(property = "spec.name", value = "DevReloadLiveReloadSpec")
    static class TestBrowserRefresh implements ReactBrowserRefresh {
        boolean active

        @Override
        boolean isActive() {
            return active
        }
    }
}

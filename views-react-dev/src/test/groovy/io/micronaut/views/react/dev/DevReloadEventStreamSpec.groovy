package io.micronaut.views.react.dev

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Property
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.sse.SseClient
import io.micronaut.http.sse.Event
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.views.react.ReactJSSourcesChangedEvent
import jakarta.inject.Inject
import reactor.core.publisher.Flux
import spock.lang.Specification

import java.time.Duration

/**
 * A rebuild reaching a browser over server-sent events, which is the default transport.
 */
@MicronautTest(environments = ["dev"], rebuildContext = true)
@Property(name = "spec.name", value = "devreload")
@Property(name = "micronaut.views.react.dev.enabled", value = "true")
class DevReloadEventStreamSpec extends Specification {
    @Inject
    EmbeddedServer server

    @Inject
    ApplicationContext context

    @Inject
    ApplicationEventPublisher<ReactJSSourcesChangedEvent> publisher

    void "a rebuild reaches a listening browser"() {
        given:
        SseClient client = context.createBean(SseClient, server.URL)

        and: """
             A rebuild nobody is listening for is dropped, so that a browser connecting later does
             not reload for one it never missed. Subscribing here does not mean the server has
             accepted the connection yet, so announce repeatedly until one lands rather than
             sleeping and hoping.
             """
        Flux<Event<String>> events = Flux.from(
            client.eventStream(HttpRequest.GET("/micronaut/views/react/dev-reload"), String))
        def rebuilds = Flux.interval(Duration.ofMillis(100))
            .doOnNext { publisher.publishEvent(new ReactJSSourcesChangedEvent(this, 1L)) }
            .subscribe()

        when:
        Event<String> first = events.blockFirst(Duration.ofSeconds(30))

        then:
        first != null
        first.name == "reload"
        first.data.isLong()

        cleanup:
        rebuilds.dispose()
        client.close()
    }
}

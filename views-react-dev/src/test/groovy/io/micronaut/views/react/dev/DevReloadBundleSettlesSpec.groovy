package io.micronaut.views.react.dev

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Property
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.views.react.ReactJSSourcesChangedEvent
import jakarta.inject.Inject
import spock.lang.Specification
import spock.lang.TempDir
import spock.util.concurrent.PollingConditions

import java.nio.file.Files
import java.nio.file.Path

/**
 * A rebuild is announced only once the bundle has stopped being written.
 *
 * <p>Announcing immediately is what made the refresh work about one time in three: the browser
 * reloads within milliseconds, and views-react re-reads the bundle lazily on that very render, so
 * it caches a half-written file and the page ends up with old markup and a current token.
 */
@MicronautTest(startApplication = false, environments = ["dev"], rebuildContext = true)
@Property(name = "micronaut.views.react.dev.enabled", value = "true")
@Property(name = "micronaut.views.react.dev.quiet-period", value = "100ms")
@Property(name = "micronaut.views.react.server-bundle-path", value = "file:build/tmp/settling-bundle.mjs")
class DevReloadBundleSettlesSpec extends Specification {
    @Inject
    ApplicationContext context

    @Inject
    ApplicationEventPublisher<ReactJSSourcesChangedEvent> publisher

    void "a rebuild is announced once the bundle stops growing"() {
        given:
        Path bundle = Path.of("build/tmp/settling-bundle.mjs").toAbsolutePath()
        Files.createDirectories(bundle.parent)
        Files.writeString(bundle, "export default {}")
        def broadcaster = context.getBean(ReactDevReloadBroadcaster)
        def before = broadcaster.currentToken()

        when: "the bundle changes"
        Files.writeString(bundle, "export default { bigger: 'than before' }")
        publisher.publishEvent(new ReactJSSourcesChangedEvent(this))

        then: "the token advances and is announced"
        new PollingConditions(timeout: 20).eventually {
            assert broadcaster.currentToken() != before
            assert broadcaster.rebuilds().blockFirst() == broadcaster.currentToken()
        }

        cleanup:
        Files.deleteIfExists(bundle)
    }

    void "the announcement runs after every other listener of the event"() {
        expect: "so the GraalJS context pool is dropped before the browser is told"
        context.getBean(ReactDevReloadBroadcaster).order == Integer.MAX_VALUE
    }
}

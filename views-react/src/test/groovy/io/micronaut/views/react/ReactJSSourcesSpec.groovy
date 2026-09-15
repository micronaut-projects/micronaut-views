package io.micronaut.views.react

import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.core.io.ResourceResolver
import io.micronaut.scheduling.io.watch.event.FileChangedEvent
import io.micronaut.scheduling.io.watch.event.WatchEventType
import org.graalvm.polyglot.Source
import spock.lang.Specification

import java.nio.file.Files

class ReactJSSourcesSpec extends Specification {

    void "source changes invalidate each source and advance the generation"() {
        given:
        def file = Files.createTempFile("react-source", ".mjs").toFile()
        file.text = "export const App = () => null"
        URL url = file.toURI().toURL()
        ResourceResolver resolver = Mock()
        resolver.getResource("bundle") >> Optional.of(url)
        resolver.getResource("render") >> Optional.of(url)
        ReactViewsRendererConfiguration configuration = Stub() {
            getServerBundlePath() >> "bundle"
            getRenderScript() >> "render"
        }
        ApplicationEventPublisher<ReactJSSourcesChangedEvent> publisher = Mock()
        ReactJSSources sources = new ReactJSSources(resolver, configuration, publisher)

        when:
        Source server = sources.serverBundle()
        sources.onApplicationEvent(new FileChangedEvent(serverPath(server), WatchEventType.MODIFY))
        Source render = sources.renderScript()
        sources.onApplicationEvent(new FileChangedEvent(serverPath(render), WatchEventType.MODIFY))
        sources.onApplicationEvent(new FileChangedEvent(serverPath(render), WatchEventType.DELETE))

        then:
        sources.generation() == 2L
        2 * publisher.publishEvent(_)

        cleanup:
        file.delete()
    }

    private static java.nio.file.Path serverPath(Source source) {
        return java.nio.file.Paths.get(source.getURI())
    }
}

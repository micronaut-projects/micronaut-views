package io.micronaut.views.react

import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.core.io.ResourceResolver
import io.micronaut.scheduling.io.watch.event.FileChangedEvent
import io.micronaut.scheduling.io.watch.event.WatchEventType
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Files
import java.nio.file.Path

/**
 * Reloading the server bundle when the file it was read from changes.
 *
 * A {@link org.graalvm.polyglot.Source} built from a {@link Reader} has a null path, so matching a
 * watch event against {@code Source.getPath()} threw out of the watch thread and killed it -- after
 * which nothing was watched at all, and nothing said so. These cover the origin the class records
 * instead.
 */
class ReactJSSourcesReloadSpec extends Specification {
    @TempDir
    Path tempDir

    private ReactJSSources sources
    private ApplicationEventPublisher<ReactJSSourcesChangedEvent> publisher
    private Path bundle
    private Path renderScript

    void setup() {
        bundle = Files.writeString(tempDir.resolve("ssr-components.mjs"), "export default {}")
        renderScript = Files.writeString(tempDir.resolve("react.js"), "// render")

        def resolver = Stub(ResourceResolver) {
            getResource("file:${bundle}".toString()) >> Optional.of(bundle.toUri().toURL())
            getResource("file:${renderScript}".toString()) >> Optional.of(renderScript.toUri().toURL())
            getResource(_ as String) >> { String path ->
                Optional.ofNullable(ReactJSSourcesReloadSpec.classLoader.getResource(path - "classpath:"))
            }
        }
        def config = Stub(ReactViewsRendererConfiguration) {
            getServerBundlePath() >> "file:${bundle}".toString()
            getRenderScript() >> "file:${renderScript}".toString()
        }
        publisher = Mock()
        sources = new ReactJSSources(resolver, config, publisher)
    }

    void "a change to the file the bundle was read from serves the new contents"() {
        given: "the bundle has been loaded"
        assert sources.serverBundle().characters.toString().contains("export default {}")

        when: "the file it came from is rewritten and the watcher reports it"
        Files.writeString(bundle, "export default { v: 2 }")
        sources.onApplicationEvent(new FileChangedEvent(bundle, WatchEventType.MODIFY))

        then: "listeners are told"
        1 * publisher.publishEvent(_ as ReactJSSourcesChangedEvent)

        and: "the next read is of the rewritten file, not the cached Source"
        sources.serverBundle().characters.toString().contains("v: 2")
    }

    void "a change to an unrelated file in the same directory does not reload"() {
        given:
        sources.serverBundle()
        sources.renderScript()

        when:
        sources.onApplicationEvent(new FileChangedEvent(tempDir.resolve("notes.txt"), WatchEventType.MODIFY))

        then:
        0 * publisher.publishEvent(_)
    }

    void "a delete is ignored"() {
        given:
        sources.serverBundle()
        sources.renderScript()

        when:
        sources.onApplicationEvent(new FileChangedEvent(bundle, WatchEventType.DELETE))

        then:
        0 * publisher.publishEvent(_)
    }

    void "a change to the render script serves the new contents"() {
        given:
        assert sources.renderScript().characters.toString().contains("// render")

        when:
        Files.writeString(renderScript, "// render v2")
        sources.onApplicationEvent(new FileChangedEvent(renderScript, WatchEventType.MODIFY))

        then:
        1 * publisher.publishEvent(_ as ReactJSSourcesChangedEvent)
        sources.renderScript().characters.toString().contains("// render v2")
    }

    void "a watch event never throws for a source with no file of its own"() {
        given: "host polyfills come off the classpath, so there is no file to match against"
        def polyfills = sources.hostPolyfills()

        when:
        sources.onApplicationEvent(new FileChangedEvent(bundle, WatchEventType.MODIFY))

        then: "this used to throw a NullPointerException out of the watch thread and kill it"
        noExceptionThrown()

        and: "and the classpath source is left alone"
        sources.hostPolyfills().is(polyfills)
    }

    void "a watch event that has not been normalised still matches the origin"() {
        given: "the same file, named through a parent-directory segment"
        sources.serverBundle()
        Files.createDirectory(tempDir.resolve("sub"))
        def unnormalised = tempDir.resolve("sub").resolve("..").resolve(bundle.fileName)

        when:
        Files.writeString(bundle, "export default { v: 3 }")
        sources.onApplicationEvent(new FileChangedEvent(unnormalised, WatchEventType.MODIFY))

        then: "matching on the raw path would miss it"
        1 * publisher.publishEvent(_ as ReactJSSourcesChangedEvent)
        sources.serverBundle().characters.toString().contains("v: 3")
    }
}

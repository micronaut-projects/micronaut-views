package io.micronaut.views.react

import io.micronaut.context.ApplicationContext
import io.micronaut.context.DefaultBeanContext
import io.micronaut.context.annotation.Requires
import io.micronaut.context.env.DevelopmentMode
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.context.reload.ResourceKind
import io.micronaut.context.watch.ResourceChange
import io.micronaut.dev.livereload.LiveReloadTrigger
import io.micronaut.scheduling.io.watch.FileChange
import io.micronaut.scheduling.io.watch.FileChangeBatch
import io.micronaut.scheduling.io.watch.FileWatcher
import io.micronaut.scheduling.io.watch.WatchOptions
import io.micronaut.scheduling.io.watch.event.FileChangedEvent
import io.micronaut.scheduling.io.watch.event.WatchEventType
import jakarta.inject.Singleton
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Files
import java.nio.file.Path
import java.util.function.Consumer

/**
 * How a rebuild of the server bundle is noticed without {@code micronaut.io.watch.paths}: through the process's
 * file watcher, or the resource watch of a development context, and how it is handed to the development launcher's
 * LiveReload server.
 */
class ReactJSSourcesWatchSpec extends Specification {

    static final String SPEC = "ReactJSSourcesWatchSpec"

    @TempDir
    Path tempDir

    Path bundle

    void setup() {
        bundle = Files.writeString(tempDir.resolve("ssr-components.mjs"), "export default {}")
    }

    void "the directory of a bundle read from a file is registered with the file watcher, and a change reloads it once"() {
        given:
        ApplicationContext context = ApplicationContext.run(properties(fileWatcher: true))
        RecordingFileWatcher watcher = context.getBean(RecordingFileWatcher)
        ReactJSSources sources = context.getBean(ReactJSSources)
        RecordingListener listener = context.getBean(RecordingListener)

        when:
        sources.serverBundle()

        then: "only the bundle's own file in its directory"
        watcher.registrations.size() == 1
        watcher.registrations[0].root == tempDir.toRealPath() || watcher.registrations[0].root == tempDir
        watcher.registrations[0].options.includeGlobs() == ["ssr-components.mjs"] as Set
        !watcher.registrations[0].options.recursive()

        when: "the rebuild is reported by the watcher, and again by a file changed event"
        Files.writeString(bundle, "export default { v: 2 }")
        watcher.registrations[0].listener.accept(new FileChangeBatch(bundle.parent, [new FileChange(bundle, WatchEventType.MODIFY)]))
        sources.onApplicationEvent(new FileChangedEvent(bundle, WatchEventType.MODIFY))

        then: "it reloads once"
        listener.generations == [1L]
        sources.serverBundle().characters.toString().contains("v: 2")

        cleanup:
        context.close()
    }

    void "outside development mode, without a file watcher, nothing is registered"() {
        given:
        ApplicationContext context = ApplicationContext.run(properties([:]))
        ReactJSSources sources = context.getBean(ReactJSSources)

        when:
        sources.serverBundle()

        then:
        !context.containsBean(FileWatcher)
        !context.containsBean(ReactBrowserRefresh)
        sources.registrations.isEmpty()
        sources.resourceWatches.isEmpty()

        cleanup:
        context.close()
    }

    void "in development mode the launcher's report of a change under a resource root reloads the bundle"() {
        given:
        ApplicationContext context = ApplicationContext.run(properties((DevelopmentMode.PROPERTY): true))
        ReactJSSources sources = context.getBean(ReactJSSources)
        RecordingListener listener = context.getBean(RecordingListener)
        sources.serverBundle()

        when:
        Files.writeString(bundle, "export default { v: 3 }")
        notify(context, ResourceKind.STATIC, [bundle])

        then:
        listener.generations == [1L]
        sources.serverBundle().characters.toString().contains("v: 3")

        when: "an unrelated file"
        notify(context, ResourceKind.STATIC, [tempDir.resolve("other.css")])

        then:
        listener.generations == [1L]

        cleanup:
        context.close()
    }

    void "with LiveReload, the browser is refreshed once the new bundle is loaded"() {
        given:
        ApplicationContext context = ApplicationContext.run(properties((DevelopmentMode.PROPERTY): true, liveReload: true))
        ReactJSSources sources = context.getBean(ReactJSSources)
        RecordingTrigger trigger = context.getBean(RecordingTrigger)
        sources.serverBundle()

        expect:
        context.getBean(ReactBrowserRefresh).isActive()

        when:
        Files.writeString(bundle, "export default { v: 4 }")
        notify(context, ResourceKind.STATIC, [bundle])

        then: "the bundle was loaded again before the browser was told"
        trigger.reloads == 1
        trigger.bundleWhenReloaded.contains("v: 4")

        when: "the same write is reported again, by another watch, after the bundle was loaded again"
        sources.onApplicationEvent(new FileChangedEvent(bundle, WatchEventType.MODIFY))

        then: "the file is as it was read: nothing is reloaded"
        trigger.reloads == 1

        when: "the bundle is half gone while the bundler writes it: loading it fails"
        Files.delete(bundle)
        notify(context, ResourceKind.STATIC, [bundle])

        then: "the browser is refreshed to show the failure"
        trigger.reloads == 2

        when: "the bundler finishes"
        Files.writeString(bundle, "export default { v: 6 }")
        notify(context, ResourceKind.STATIC, [bundle])

        then: "the write is reloaded, though nothing was cached"
        trigger.reloads == 3
        trigger.bundleWhenReloaded.contains("v: 6")

        when: "the server is off"
        trigger.enabled = false
        Files.writeString(bundle, "export default { v: 5 }")
        notify(context, ResourceKind.STATIC, [bundle])

        then:
        trigger.reloads == 3
        !context.getBean(ReactBrowserRefresh).isActive()

        cleanup:
        context.close()
    }

    private Map<String, Object> properties(Map<String, Object> more) {
        Map<String, Object> properties = new HashMap<>()
        properties.put("spec.name", SPEC)
        properties.put("micronaut.views.react.server-bundle-path", "file:" + bundle)
        more.each { k, v ->
            if (k == "fileWatcher") {
                properties.put("spec.file-watcher", v)
            } else if (k == "liveReload") {
                properties.put("spec.live-reload", v)
            } else {
                properties.put(k, v)
            }
        }
        return properties
    }

    private void notify(ApplicationContext context, ResourceKind kind, List<Path> changed) {
        ((DefaultBeanContext) context).notifyResourceChange(new ResourceChange(kind, [tempDir], changed, [], false))
    }

    @Singleton
    @Requires(property = "spec.name", value = SPEC)
    static class RecordingListener implements ApplicationEventListener<ReactJSSourcesChangedEvent> {
        final List<Long> generations = []

        @Override
        void onApplicationEvent(ReactJSSourcesChangedEvent event) {
            generations << event.generation()
        }
    }

    @Singleton
    @Requires(property = "spec.file-watcher", value = "true")
    static class RecordingFileWatcher implements FileWatcher {
        final List<Map<String, Object>> registrations = []

        @Override
        FileWatcher.Registration watch(Path root, WatchOptions options, Consumer<FileChangeBatch> listener) {
            registrations << [root: root, options: options, listener: listener]
            return new FileWatcher.Registration() {
                Path root() { root }
                WatchOptions options() { options }
                boolean isActive() { true }
                void close() { }
            }
        }

        @Override
        boolean isWatching(Path path) {
            return false
        }
    }

    @Singleton
    @Requires(property = "spec.live-reload", value = "true")
    static class RecordingTrigger implements LiveReloadTrigger {
        final ReactJSSources sources
        boolean enabled = true
        int reloads
        String bundleWhenReloaded

        RecordingTrigger(ReactJSSources sources) {
            this.sources = sources
        }

        @Override
        boolean isEnabled() {
            return enabled
        }

        @Override
        void reload() {
            reloads++
            // what is loaded at this moment, without loading it
            def loaded = sources.@serverBundle
            bundleWhenReloaded = loaded == null ? null : loaded.characters.toString()
        }

        @Override
        void reload(Path path) {
            reload()
        }

        @Override
        void reloadCss(Path stylesheet) {
        }
    }
}

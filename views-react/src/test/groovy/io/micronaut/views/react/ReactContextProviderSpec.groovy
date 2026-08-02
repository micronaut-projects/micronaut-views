package io.micronaut.views.react

import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Source
import spock.lang.Specification

class ReactContextProviderSpec extends Specification {

    void "custom providers receive rendering and source invalidation callbacks without owning renderer cache"() {
        given:
        Context context = Context.newBuilder("js")
            .allowAllAccess(true)
            .option("js.esm-eval-returns-exports", "true")
            .build()
        Source serverBundle = Source.newBuilder("js", "export function App() { return null }", "bundle.mjs")
            .mimeType("application/javascript+module")
            .build()
        Source renderScript = Source.newBuilder("js", "export function ssr(component, props, callback) { callback.write('rendered') }", "render.mjs")
            .mimeType("application/javascript+module")
            .build()
        ReactContextProvider provider = Mock()
        provider.withContext(_) >> { args -> args[0].apply(context) }
        ReactViewsRendererConfiguration configuration = Stub() {
            getClientBundleURL() >> "/static/client.js"
            getServerBundlePath() >> "bundle.mjs"
            getRenderScript() >> "render.mjs"
        }
        ReactJSSources sources = Mock()
        sources.generation() >> 0L
        sources.serverBundle() >> serverBundle
        sources.renderScript() >> renderScript
        ReactViewsRenderer renderer = new ReactViewsRenderer(provider, configuration, sources)

        when:
        String result = WritableUtils.writableToString(renderer.render("App", [:], null)).orElseThrow()
        renderer.onApplicationEvent(new ReactJSSourcesChangedEvent(sources, 1L))
        context.eval("js", "1")

        then:
        result == "rendered"
        1 * provider.sourcesChanged(1L)

        cleanup:
        context?.close()
    }
}

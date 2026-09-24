package io.micronaut.views.react

import io.micronaut.views.react.util.BeanPool
import io.micronaut.http.exceptions.MessageBodyException
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Source
import spock.lang.Specification

class ReactContextProviderSpec extends Specification {

    void "renderer reports a missing SSR function"() {
        given:
        Context context = Context.newBuilder("js").option("js.esm-eval-returns-exports", "true").build()
        ReactViewsRenderer renderer = renderer(context,
            module("export function App() { return null }", "bundle.mjs"),
            module("export const other = 1", "render.mjs"))

        when:
        renderer.render("App", [:], null).writeTo(OutputStream.nullOutputStream())

        then:
        thrown(MessageBodyException)

        cleanup:
        context.close()
    }

    void "renderer reports an unknown component"() {
        given:
        Context context = Context.newBuilder("js").option("js.esm-eval-returns-exports", "true").build()
        ReactViewsRenderer renderer = renderer(context,
            module("export function Other() { return null }", "bundle.mjs"),
            module("export function ssr(component, props, callback) { callback.write('rendered') }", "render.mjs"))

        when:
        renderer.render("App", [:], null).writeTo(OutputStream.nullOutputStream())

        then:
        thrown(MessageBodyException)

        cleanup:
        context.close()
    }

    void "provider source invalidation is optional"() {
        given:
        ReactContextProvider provider = { callback -> "result" }

        expect:
        provider.withContext { "ignored" } == "result"
        provider.sourcesChanged(1L)
    }

    void "default provider clears its owned context pool on source changes"() {
        given:
        Context context = Context.newBuilder("js").build()
        BeanPool<ReactJSContext> pool = new BeanPool({ new ReactJSContext(context) })
        ReactContextProvider provider = new DefaultReactContextProvider(pool)

        expect:
        provider.withContext { it == context }

        when:
        provider.sourcesChanged(1L)
        context.eval("js", "1")

        then:
        thrown(IllegalStateException)

        cleanup:
        context.close()
    }

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
        sources.generation() >>> [0L, 1L]
        sources.serverBundle() >> serverBundle
        sources.renderScript() >> renderScript
        ReactViewsRenderer renderer = new ReactViewsRenderer(provider, configuration, sources)

        when:
        String result = WritableUtils.writableToString(renderer.render("App", [:], null)).orElseThrow()
        boolean exists = renderer.exists("App")
        boolean missing = renderer.exists("Missing")
        renderer.onApplicationEvent(new ReactJSSourcesChangedEvent(sources, 1L))
        String reloaded = WritableUtils.writableToString(renderer.render("App", [:], null)).orElseThrow()
        context.eval("js", "1")

        then:
        result == "rendered"
        reloaded == "rendered"
        exists
        !missing
        1 * provider.sourcesChanged(1L)

        cleanup:
        context?.close()
    }

    private static Source module(String source, String name) {
        Source.newBuilder("js", source, name)
            .mimeType("application/javascript+module")
            .build()
    }

    private ReactViewsRenderer renderer(Context context, Source serverBundle, Source renderScript) {
        ReactContextProvider provider = { callback -> callback.apply(context) }
        ReactViewsRendererConfiguration configuration = Stub() {
            getClientBundleURL() >> "/static/client.js"
            getServerBundlePath() >> "bundle.mjs"
            getRenderScript() >> "render.mjs"
        }
        ReactJSSources sources = Mock()
        sources.generation() >> 0L
        sources.serverBundle() >> serverBundle
        sources.renderScript() >> renderScript
        new ReactViewsRenderer(provider, configuration, sources)
    }
}

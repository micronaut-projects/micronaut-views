package io.micronaut.views.react

import io.micronaut.context.annotation.Property
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import org.graalvm.polyglot.Context
import spock.lang.Specification

/**
 * The globals an engine embedded in a JVM does not provide, but React's server renderer and React
 * Router reach for. Evaluated in a real GraalJS context, which is the only place the absence shows:
 * Node has all of these natively, so a bundle checked under Node proves nothing about them.
 */
@MicronautTest(startApplication = false)
@Property(name = "micronaut.views.react.server-bundle-path", value = "classpath:views/ssr-components.mjs")
class HostPolyfillsSpec extends Specification {
    @Inject
    ReactJSSources sources

    private static Object evaluate(ReactJSSources sources, String expression) {
        try (Context context = Context.newBuilder("js").allowAllAccess(true).build()) {
            context.eval(sources.hostPolyfills())
            return context.eval("js", expression).as(Object)
        }
    }

    void "MessageChannel is installed, because React 19's scheduler requires it"() {
        expect:
        evaluate(sources, "typeof globalThis.MessageChannel") == "function"
    }

    void "URLSearchParams reads a query string"() {
        expect:
        evaluate(sources, "new URLSearchParams('?a=1&b=two&a=3').get('a')") == "1"
        evaluate(sources, "new URLSearchParams('?a=1&a=3').getAll('a').join(',')") == "1,3"
        evaluate(sources, "new URLSearchParams('?a=1').has('b')") == false
        evaluate(sources, "new URLSearchParams('?a=hello%20world').get('a')") == "hello world"
    }

    void "URL parses the parts React Router asks for"() {
        expect:
        evaluate(sources, "new URL('https://example.com/items/4?page=2#top').pathname") == "/items/4"
        evaluate(sources, "new URL('https://example.com/items?page=2').search") == "?page=2"
        evaluate(sources, "new URL('https://example.com/items?page=2').searchParams.get('page')") == "2"
        evaluate(sources, "new URL('https://example.com/a#b').hash") == "#b"
        evaluate(sources, "new URL('https://example.com/a').origin") == "https://example.com"
    }

    void "a relative URL resolves against the base, as the router builds them"() {
        expect:
        evaluate(sources, "new URL('/login', 'https://example.com').href") == "https://example.com/login"
        evaluate(sources, "new URL('login', 'https://example.com/').pathname") == "/login"
    }
}

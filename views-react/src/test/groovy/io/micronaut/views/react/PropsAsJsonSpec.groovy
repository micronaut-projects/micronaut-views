package io.micronaut.views.react

import io.micronaut.context.ApplicationContext
import io.micronaut.context.BeanProvider
import io.micronaut.http.HttpRequest
import io.micronaut.http.exceptions.MessageBodyException
import io.micronaut.json.JsonMapper
import spock.lang.Specification

import java.time.Instant

class PropsAsJsonSpec extends Specification {

    static final CardProps PROPS = new CardProps("Middlemarch", CardProps.Genre.NOVEL, Instant.parse("2026-11-27T10:15:30Z"), 880, "not for the page", ["classic", "long"])

    private static String render(ApplicationContext context, String component, Object props, HttpRequest<?> request = null) {
        WritableUtils.writableToString(context.getBean(ReactViewsRenderer).render(component, props, request)).orElseThrow()
    }

    void "a component is given what the JSON of its props says, and so is the browser"() {
        given:
        ApplicationContext context = ApplicationContext.run('micronaut.views.react.props-as-json': true)
        String json = context.getBean(JsonMapper).writeValueAsString(PROPS)

        when:
        String result = render(context, "Card", PROPS)

        then: "the JSON is what a controller would answer with"
        json == '{"title":"Middlemarch","genre":"NOVEL","added":"2026-11-27T10:15:30Z","pageCount":880,"tags":["classic","long"]}'

        and: "the component read it"
        result.contains("<title>Middlemarch</title>")
        result.contains("Genre: <!-- -->NOVEL")
        result.contains("Added: <!-- -->2026-11-27T10:15:30Z")
        result.contains("Pages: <!-- -->880")
        result.contains("<li>classic</li><li>long</li>")

        and: "the browser is handed the same, and nothing JSON leaves out"
        result.contains('"rootProps":' + json)
        !result.contains("not for the page")

        cleanup:
        context.close()
    }

    void "the address of the request is added to the props as before"() {
        given:
        ApplicationContext context = ApplicationContext.run('micronaut.views.react.props-as-json': true)
        HttpRequest<?> request = Mock()
        request.getUri() >> URI.create("https://localhost/demopage")

        when:
        String result = render(context, "Card", PROPS, request)

        then:
        result.contains('"pageCount":880,"tags":["classic","long"],"url":"https://localhost/demopage"')
        result.contains("/static/client.js")

        cleanup:
        context.close()
    }

    void "a map and no props at all are props too"() {
        given:
        ApplicationContext context = ApplicationContext.run('micronaut.views.react.props-as-json': true)

        expect:
        render(context, "Card", [title: "A map", genre: CardProps.Genre.ESSAY, added: Instant.parse("2026-11-27T10:15:30Z"), pageCount: 12, tags: []])
            .contains("Genre: <!-- -->ESSAY")

        when: "a component that needs props is given none"
        render(context, "Card", null)

        then: "the render fails in the component, and not before it"
        MessageBodyException e = thrown()
        e.message == "Could not render component Card"

        cleanup:
        context.close()
    }

    void "what the mapper leaves out is not in the props, and what it is told to include is"() {
        given:
        CardProps noTags = new CardProps("Short", CardProps.Genre.ESSAY, Instant.parse("2026-11-27T10:15:30Z"), 12, null, [])

        when: "Micronaut Serialization leaves an empty list out unless it is told otherwise"
        ApplicationContext context = ApplicationContext.run('micronaut.views.react.props-as-json': true)
        render(context, "Card", noTags)

        then: "the component, which maps over the list, finds none"
        thrown(MessageBodyException)

        when:
        context.close()
        context = ApplicationContext.run('micronaut.views.react.props-as-json': true, 'micronaut.serde.serialization.inclusion': 'ALWAYS')
        String result = render(context, "Card", noTags)

        then:
        result.contains("Genre: <!-- -->ESSAY")
        result.contains('"tags":[]')

        cleanup:
        context.close()
    }

    void "a configuration compiled before there was such a setting has it off"() {
        given:
        ApplicationContext context = ApplicationContext.run()
        ReactViewsRendererConfiguration bound = context.getBean(ReactViewsRendererConfiguration)
        ReactViewsRendererConfiguration older = Stub() {
            getClientBundleURL() >> bound.clientBundleURL
            isHydrateWithoutRequest() >> true
            isPropsAsJson() >> { throw new AbstractMethodError() }
        }
        ReactViewsRenderer renderer = new ReactViewsRenderer(context.getBean(ReactContextProvider), older, context.getBean(ReactJSSources), [], Stub(BeanProvider))

        expect: "the props are the Java object, whose method the component calls"
        WritableUtils.writableToString(renderer.render("App", TestProps.basic, null)).orElseThrow().contains("Calling a method works: <!-- -->Goodbye Bob!")

        cleanup:
        context.close()
    }

    void "without the setting the component reads the Java object, where an enum is no string"() {
        given:
        ApplicationContext context = ApplicationContext.run()

        when:
        render(context, "Card", PROPS)

        then: "React takes no object as a child"
        thrown(MessageBodyException)

        cleanup:
        context.close()
    }
}

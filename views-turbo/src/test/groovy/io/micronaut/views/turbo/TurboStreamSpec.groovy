package io.micronaut.views.turbo

import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.annotation.Nullable
import io.micronaut.core.io.Writable
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Produces
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.views.View
import io.micronaut.views.ViewsRenderer
import io.micronaut.views.turbo.http.TurboHttpHeaders
import io.micronaut.views.turbo.http.TurboMediaType
import jakarta.inject.Inject
import jakarta.inject.Singleton
import spock.lang.Specification
import spock.lang.Unroll

@Property(name = "micronaut.http.client.follow-redirects", value = StringUtils.FALSE)
@Property(name = "spec.name", value = "TurboStreamSpec")
@MicronautTest
class TurboStreamSpec extends Specification {

    @Inject
    @Client("/")
    HttpClient httpClient

    void "turbo stream append with target DOM ID and content"() {
        given:
        String template = "Content to append to container designated with the dom_id."
        String domId = "dom_id"
        TurboStreamAction action = TurboStreamAction.APPEND
        when:
        TurboStream turboStream = TurboStream.builder()
                .targetDomId(domId)
                .action(action)
                .template(template)
                .build()
        then:
        domId == turboStream.targetDomId.get()
        action == turboStream.action
        template == turboStream.template.get()

        when:
        turboStream = TurboStream.builder()
                .targetDomId(domId)
                .template(template)
                .append()
                .build()
        then:
        domId == turboStream.targetDomId.get()
        action == turboStream.action
        template == turboStream.template.get()
    }

    void "action methods populate action and invoke build"() {
        given:
        String domId = "dom_id"

        expect:
        TurboStreamAction.REMOVE == TurboStream.builder().targetDomId(domId).remove().build().getAction()
        TurboStreamAction.APPEND == TurboStream.builder().targetDomId(domId).append().build().getAction()
        TurboStreamAction.PREPEND == TurboStream.builder().targetDomId(domId).prepend().build().getAction()
        TurboStreamAction.AFTER == TurboStream.builder().targetDomId(domId).after().build().getAction()
        TurboStreamAction.BEFORE == TurboStream.builder().targetDomId(domId).before().build().getAction()
        TurboStreamAction.UPDATE == TurboStream.builder().targetDomId(domId).update().build().getAction()
        TurboStreamAction.REPLACE == TurboStream.builder().targetDomId(domId).replace().build().getAction()
    }

    void "template is not required"() {
        given:
        String domId = "dom_id"
        when:
        TurboStream turboStream = TurboStream.builder()
                .targetDomId(domId)
                .remove()
                .build()
        then:
        domId == turboStream.targetDomId.get()
        TurboStreamAction.REMOVE == turboStream.action
        !turboStream.template
    }

    void "TurboStream are rendered"() {
        given:
        BlockingHttpClient client = httpClient.toBlocking()

        when:
        HttpResponse<String> responseHtml = client.exchange(HttpRequest.GET("/turbo/append").accept(TurboMediaType.TURBO_STREAM, MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML), String)

        then:
        HttpStatus.OK == responseHtml.status()
        responseHtml.contentType.isPresent()
        'text/vnd.turbo-stream.html' == responseHtml.contentType.get().toString()

        when:
        String html = responseHtml.body()

        then:
        "<turbo-stream action=\"append\" target=\"dom_id\"><template>Content to append to container designated with the dom_id.</template></turbo-stream>" == html

        when:
        html = client.retrieve(HttpRequest.GET("/turbo/remove").accept(TurboMediaType.TURBO_STREAM, MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML))

        then:
        "<turbo-stream action=\"remove\" target=\"dom_id\"></turbo-stream>" == html

        when:
        html = client.retrieve(HttpRequest.GET("/turbo/remove/nobuilder").accept(TurboMediaType.TURBO_STREAM, MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML))

        then:
        "<turbo-stream action=\"remove\" target=\"dom_id\"></turbo-stream>" == html

        when:
        html = client.retrieve(HttpRequest.GET("/turbo/update")
                .accept(TurboMediaType.TURBO_STREAM, MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML)
                .header(TurboHttpHeaders.TURBO_FRAME, "main-container"))

        then:
        "<turbo-stream action=\"update\" target=\"main-container\"><template><div class=\"message\">Hello World</div></template></turbo-stream>" == html
    }

    void "list of TurboStream are rendered"() {
        given:
        BlockingHttpClient client = httpClient.toBlocking()

        when:
        String html = client.retrieve(HttpRequest.GET("/turbo/list").accept(TurboMediaType.TURBO_STREAM, MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML))

        then:
        "<turbo-stream action=\"remove\" target=\"dom_id\"></turbo-stream>" +
                "<turbo-stream action=\"update\" target=\"main-container\"><template><div class=\"message\">Hello World</div></template></turbo-stream>" == html
    }

    void "verify TurboView annotation can specify targetDomId"() {
        given:
        BlockingHttpClient client = httpClient.toBlocking()

        when:
        String html = client.retrieve(HttpRequest.GET("/turbo/targetDomId").accept(TurboMediaType.TURBO_STREAM, MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML))

        then:
        "<turbo-stream action=\"update\" target=\"main-container\"><template><div class=\"message\">Hello World</div></template></turbo-stream>" == html
    }

    void "verify TurboView annotation can specify requestId"() {
        given:
        BlockingHttpClient client = httpClient.toBlocking()

        when:
        String html = client.retrieve(HttpRequest.GET("/turbo/requestId").accept(TurboMediaType.TURBO_STREAM, MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML))

        then:
        '<turbo-stream action="refresh" request-id="abcd-1234"><template><div class="message">Hello World</div></template></turbo-stream>' == html
    }

    void "verify TurboView annotation can specify childrenOnly"() {
        given:
        BlockingHttpClient client = httpClient.toBlocking()

        when:
        String html = client.retrieve(HttpRequest.GET("/turbo/childrenOnly").accept(TurboMediaType.TURBO_STREAM, MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML))

        then:
        '<turbo-stream action="morph" children-only><template><div class="message">Hello World</div></template></turbo-stream>' == html
    }

    void "verify TurboView annotation can specify targetCssQuerySelector"() {
        given:
        BlockingHttpClient client = httpClient.toBlocking()

        when:
        String html = client.retrieve(HttpRequest.GET("/turbo/targetCssQuerySelector").accept(TurboMediaType.TURBO_STREAM, MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML))

        then:
        "<turbo-stream action=\"after\" targets=\".elementsWithClass\"><template><div class=\"message\">Hello World</div></template></turbo-stream>" == html
    }

    void "verify a method annotated with TurboView and return void"() {
        given:
        BlockingHttpClient client = httpClient.toBlocking()

        when:
        String html = client.retrieve(HttpRequest.GET("/turbo/del")
                .header(TurboHttpHeaders.TURBO_FRAME, "main-container")
                .accept(TurboMediaType.TURBO_STREAM, MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML))

        then:
        "<turbo-stream action=\"remove\" target=\"main-container\"></turbo-stream>" == html
    }

    void "verify a route could have both View and TurboView annotations. if it is a turbo request the TurboView Annotation is used"() {
        given:
        BlockingHttpClient client = httpClient.toBlocking()

        when: 'if it is not a turbo request, then render the view specified with the View annotation'
        HttpResponse<String> responseTurbo = client.exchange(HttpRequest.GET("/turbo/withBothAnnotations")
                .header(TurboHttpHeaders.TURBO_FRAME, "main-container")
                .accept(TurboMediaType.TURBO_STREAM, MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML), String)

        then:
        HttpStatus.OK == responseTurbo.status()
        'text/vnd.turbo-stream.html' == responseTurbo.contentType.get().toString()
        "<turbo-stream action=\"update\" target=\"main-container\"><template><div class=\"message\">Hello World</div></template></turbo-stream>" == responseTurbo.body()
    }

    void "verify a route could have both View and TurboView annotations. if it is not a turbo request the View Annotation is used"() {
        given:
        BlockingHttpClient client = httpClient.toBlocking()

        when: 'if it is not a turbo request, then render the view specified with the View annotation'
        HttpResponse<String> responseHtml = client.exchange(HttpRequest.GET("/turbo/withBothAnnotations")
                .accept(MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML), String)

        then:
        HttpStatus.OK == responseHtml.status()
        responseHtml.contentType.isPresent()
        responseHtml.contentType.get().toString() == MediaType.TEXT_HTML
        "<!DOCTYPE html><html><head><title>Page Title</title></head><body><h1>Hello World</h1></body></html>" == responseHtml.body()
    }

    @Unroll
    void "target CSS Query Selector must have letters, digits, hyphens underscores colons, and periods"(String domId) {
        when:
        TurboStream.builder()
                .targetCssQuerySelector(domId)
                .replace()
                .build()

        then:
        noExceptionThrown()

        where:
        domId << [
                "a",
                "1",
                ".",
                ":",
                "-",
                "_",
        ]
    }

    @Unroll
    void "Illegal argument exception thrown if target CSS Query Selector contains something but letters, digits, hyphens underscores colons, and periods"(String domId) {
        when:
        TurboStream.builder()
                .targetCssQuerySelector(domId)
                .replace()
                .build()

        then:
        thrown(IllegalArgumentException)

        where:
        domId << [
                "*",
                "?",
                '"'
        ]
    }

    @Unroll
    void "target CSS Query Selector validation can be disabled"(String domId) {
        when:
        TurboStream.builder()
                .targetCssQuerySelector(domId)
                .targetCssQuerySelectorPattern(null)
                .replace()
                .build()

        then:
        noExceptionThrown()

        where:
        domId << [
                "*",
                "?",
                '"'
        ]
    }

    @Unroll
    void "target DOM Id attribute must begin with a letter and may be followed by any number of letters, digits, hyphens underscores colons, and periods"(String domId) {
        when:
        TurboStream.builder()
                .targetDomId(domId)
                .replace()
                .build()

        then:
        noExceptionThrown()

        where:
        domId << [
                "a",
                "ab",
                "a1",
                "a.",
                "a:",
                "a-",
                "a_",
        ]
    }

    @Unroll
    void "Illegal argument exception thrown if target DOM Id attribute does not begin with a letter and may be followed by any number of letters, digits, hyphens underscores colons, and periods"(String domId) {
        when:
        TurboStream.builder()
                .targetDomId(domId)
                .replace()
                .build()

        then:
        thrown(IllegalArgumentException)

        where:
        domId << [
                "a*",
                "a?",
                'a"',
                "1",
                ".",
                ":",
                "-",
                "_",
        ]
    }

    @Unroll
    void "Target DOM Id attribute validation can be disabled"(String domId) {
        when:
        TurboStream.builder()
                .targetDomId(domId)
                .targetDomIdPattern(null)
                .replace()
                .build()

        then:
        noExceptionThrown()

        where:
        domId << [
                "a*",
                "a?",
                'a"',
                "1",
                ".",
                ":",
                "-",
                "_",
        ]
    }

    void "Turbo Stream not answered unless Accept header contains turbo-stream"() {
        given:
        BlockingHttpClient client = httpClient.toBlocking()

        when:
        HttpRequest<?> request = HttpRequest.POST("/customers/delete", Collections.emptyMap())
                .header(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
        HttpResponse<String> response = client.exchange(request, String)

        then:
        HttpStatus.SEE_OTHER == response.status()
        null == response.body()
        !response.contentType.isPresent()

        when:
        request = HttpRequest.POST("/customers/delete", Collections.emptyMap())
                .header(HttpHeaders.ACCEPT, "text/vnd.turbo-stream.html, text/html, application/xhtml+xml")
        response = client.exchange(request, String)

        then:
        '<turbo-stream action="remove" target="foo"></turbo-stream>' == response.body()
        response.contentType.isPresent()
        response.contentType.get() == TurboMediaType.TURBO_STREAM_TYPE
    }

    @Requires(property = "spec.name", value = "TurboStreamSpec")
    @Controller("/turbo")
    static class TurboStreamWriteableController {
        @Produces(TurboMediaType.TURBO_STREAM)
        @Get("/append")
        TurboStream.Builder index() {
            TurboStream.builder()
                    .targetDomId("dom_id")
                    .template("Content to append to container designated with the dom_id.")
                    .append()
        }

        @Produces(TurboMediaType.TURBO_STREAM)
        @Get("/remove")
        TurboStream.Builder remove() {
            TurboStream.builder()
                    .targetDomId("dom_id")
                    .remove()
        }

        @Produces(TurboMediaType.TURBO_STREAM)
        @Get("/remove/nobuilder")
        TurboStream removeNoBuilder() {
            TurboStream.builder()
                    .targetDomId("dom_id")
                    .remove()
                    .build()
        }

        @Produces(TurboMediaType.TURBO_STREAM)
        @Get("/list")
        List<TurboStream.Builder> turboList() {
            List.of(
                    TurboStream.builder().targetDomId("dom_id").remove(),
                    TurboStream.builder().targetDomId("main-container").template("fragments/message", "Hello World").update()
            )
        }

        @Produces(TurboMediaType.TURBO_STREAM)
        @TurboStreamView("fragments/message")
        @Get("/update")
        String update() {
            "Hello World"
        }

        @Produces(TurboMediaType.TURBO_STREAM)
        @TurboStreamView(value = "fragments/message", targetDomId = "main-container")
        @Get("/targetDomId")
        String targetDomId() {
            "Hello World"
        }

        @Produces(TurboMediaType.TURBO_STREAM)
        @TurboStreamView(value = "fragments/message", action=TurboStreamAction.REFRESH, requestId = "abcd-1234")
        @Get("/requestId")
        String requestId() {
            "Hello World"
        }

        @Produces(TurboMediaType.TURBO_STREAM)
        @TurboStreamView(value = "fragments/message", action=TurboStreamAction.MORPH, childrenOnly = true)
        @Get("/childrenOnly")
        String childrenOnly() {
            "Hello World"
        }

        @Produces(TurboMediaType.TURBO_STREAM)
        @TurboStreamView(action = TurboStreamAction.REMOVE)
        @Get("/del")
        void del() {
        }

        @Produces(TurboMediaType.TURBO_STREAM)
        @TurboStreamView(value = "fragments/message", targetCssQuerySelector = ".elementsWithClass", action = TurboStreamAction.AFTER)
        @Get("/targetCssQuerySelector")
        String targetCssQuerySelector() {
            "Hello World"
        }

        @Produces(value = [MediaType.TEXT_HTML, TurboMediaType.TURBO_STREAM])
        @View("home")
        @TurboStreamView(value = "fragments/message")
        @Get("/withBothAnnotations")
        String withBothAnnotations() {
            "Hello World"
        }
    }

    @Requires(property = "spec.name", value = "TurboStreamSpec")
    @Singleton
    static class MockViewsRenderer<T> implements ViewsRenderer<T, HttpRequest<?>> {

        @Override
        @NonNull
        Writable render(@NonNull String viewName,
                        @Nullable T data,
                        @Nullable HttpRequest<?> request) {
            if (viewName == 'home') {
                return new Writable() {
                    @Override
                    void writeTo(Writer out) throws IOException {
                        out.write("<!DOCTYPE html><html><head><title>Page Title</title></head><body><h1>" + data.toString() + "</h1></body></html>")
                    }
                }
            }
            return (writer) -> {
                if (data != null) {
                    writer.write("<div class=\"message\">")
                    writer.write(data.toString())
                    writer.write("</div>")
                }
            }
        }

        @Override
        boolean exists(@NonNull String viewName) {
            true
        }
    }

    @Requires(property = "spec.name", value = "TurboStreamSpec")
    @Controller("/customers")
    static class CustomersController {

        @Get
        Map<String, Object> index() {
            [:]
        }

        @Produces([MediaType.TEXT_HTML, TurboMediaType.TURBO_STREAM])
        @TurboStreamView(action = TurboStreamAction.REMOVE, targetDomId = "foo")
        @Post("/delete")
        HttpResponse<?> delete() {
            return HttpResponse.seeOther(URI.create("/customers"))
        }
    }

}

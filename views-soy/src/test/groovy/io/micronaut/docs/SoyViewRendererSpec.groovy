package io.micronaut.docs

import io.micronaut.context.ApplicationContext
import io.micronaut.core.io.Writable
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.views.ViewsFilter
import io.micronaut.views.soy.SoyTofuViewsRenderer
import io.micronaut.views.soy.SoyTofuViewsRendererConfigurationProperties
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import java.nio.charset.StandardCharsets


class SoyViewRendererSpec extends Specification {
    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer,
    [
            "spec.name": "soy",
            "micronaut.views.soy.enabled": true,
            'micronaut.views.thymeleaf.enabled': false,
            'micronaut.views.velocity.enabled': false,
            'micronaut.views.handlebars.enabled': false,
            'micronaut.views.freemarker.enabled': false,
    ],
    "test")

    @Shared
    @AutoCleanup
    RxHttpClient client = embeddedServer.getApplicationContext().createBean(RxHttpClient, embeddedServer.getURL())

    def "bean is loaded"() {
        when:
        embeddedServer.applicationContext.getBean(SoyTofuViewsRenderer)
        embeddedServer.applicationContext.getBean(ViewsFilter)

        then:
        noExceptionThrown()

        when:
        SoyTofuViewsRendererConfigurationProperties props = embeddedServer.applicationContext.getBean(
                SoyTofuViewsRendererConfigurationProperties)

        then:
        props.isEnabled()
    }

    def "invoking /soy/home does not specify @View, thus, regular JSON rendering is used"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/soy/home', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("loggedIn")
        rsp.body().contains("sgammon")
        rsp.contentType.isPresent()
        rsp.contentType.get() == MediaType.APPLICATION_JSON_TYPE
    }

    def "invoking /soy renders soy template from a controller returning a map"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/soy', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("<h1>username: <span>sgammon</span></h1>")
    }

    def "invoking /soy/missing should produce a 404 exception describing a missing view template"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/soy/missing', String)

        then:
        def e = thrown(HttpClientResponseException)

        and:
        e.status == HttpStatus.NOT_FOUND
    }

    def "invoking /soy/invalidContext should produce an exception describing invalid context"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/soy/invalidContext', String)

        then:
        def e = thrown(HttpClientResponseException)

        and:
        e.status == HttpStatus.INTERNAL_SERVER_ERROR
    }

    def "AppendableToWritable should work as an Appendable and a Writable"() {
        when:
        SoyTofuViewsRenderer.AppendableToWritable obj = new SoyTofuViewsRenderer.AppendableToWritable()
        Appendable objAsAppendable = obj
        Writable objAsWritable = obj
        obj.append("hello 123")
        objAsAppendable.append("456789", 3, 5)
        objAsAppendable.append("0".toCharArray()[0])

        then:
        noExceptionThrown()

        when:
        OutputStream outputStream = new ByteArrayOutputStream()
        objAsWritable.writeTo(outputStream, StandardCharsets.UTF_8)
        String encoded = new String(outputStream.toByteArray(), StandardCharsets.UTF_8)

        then:
        encoded == "hello 123780"
    }
}

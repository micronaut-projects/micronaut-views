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
import io.micronaut.views.csp.CspFilter
import io.micronaut.views.soy.AppendableToWritable
import io.micronaut.views.soy.SoySauceViewsRenderer
import io.micronaut.views.soy.SoyViewsRendererConfigurationProperties
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.util.regex.Pattern


class SoySauceViewRendererSpec extends Specification {

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer,
    [
            "spec.name": "soy",
            "micronaut.security.enabled": false,
            "micronaut.views.soy.engine": "sauce",
            'micronaut.views.csp.enabled': true,
            'micronaut.views.csp.generateNonce': true,
            'micronaut.views.csp.reportOnly': false,
            'micronaut.views.csp.policyDirectives': "default-src self:; script-src 'nonce-{#nonceValue}';"
    ],
    "test")

    @Shared
    @AutoCleanup
    RxHttpClient client = embeddedServer.getApplicationContext().createBean(RxHttpClient, embeddedServer.getURL())

    def "bean is loaded"() {
        when:
        embeddedServer.applicationContext.getBean(SoySauceViewsRenderer)
        embeddedServer.applicationContext.getBean(ViewsFilter)

        then:
        noExceptionThrown()

        when:
        SoyViewsRendererConfigurationProperties props = embeddedServer.applicationContext.getBean(
                SoyViewsRendererConfigurationProperties)

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
        client.toBlocking().exchange('/soy/missing', String)

        then:
        def e = thrown(HttpClientResponseException)

        and:
        e.status == HttpStatus.INTERNAL_SERVER_ERROR
    }

    def "AppendableToWritable should work as an Appendable and a Writable"() {
        when:
        AppendableToWritable obj = new AppendableToWritable()
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

    def "invoking /soy renders soy template with built-in CSP nonce support"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/soy', String)
        def headerNames = rsp.headers.names()

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK
        headerNames.contains(CspFilter.CSP_HEADER)
        rsp.header(CspFilter.CSP_HEADER).contains("default-src self:;")
        rsp.header(CspFilter.CSP_HEADER).contains("'nonce-")
        !headerNames.contains(CspFilter.CSP_REPORT_ONLY_HEADER)
        def nonceValue = rsp.header(CspFilter.CSP_HEADER)
                .find(Pattern.compile("nonce-(.*)"))
                .replace("';", "")
                .replace("nonce-", "")

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("<h1>username: <span>sgammon</span></h1>")
        rsp.body().contains("nonce=\"${nonceValue}\"")
    }

    def "invoking /soy renders soy template with CSP nonce that changes with each invocation"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/soy', String)
        HttpResponse<String> rsp2 = client.toBlocking().exchange('/soy', String)
        def headerNames = rsp.headers.names()
        def headerNames2 = rsp2.headers.names()

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK
        rsp2.status() == HttpStatus.OK
        headerNames.contains(CspFilter.CSP_HEADER)
        headerNames2.contains(CspFilter.CSP_HEADER)
        rsp.header(CspFilter.CSP_HEADER).contains("default-src self:;")
        rsp2.header(CspFilter.CSP_HEADER).contains("default-src self:;")
        rsp.header(CspFilter.CSP_HEADER).contains("'nonce-")
        rsp2.header(CspFilter.CSP_HEADER).contains("'nonce-")
        !headerNames.contains(CspFilter.CSP_REPORT_ONLY_HEADER)
        !headerNames2.contains(CspFilter.CSP_REPORT_ONLY_HEADER)
        def nonceValue = rsp.header(CspFilter.CSP_HEADER)
                .find(Pattern.compile("nonce-(.*)"))
                .replace("';", "")
                .replace("nonce-", "")
        def nonceValue2 = rsp2.header(CspFilter.CSP_HEADER)
                .find(Pattern.compile("nonce-(.*)"))
                .replace("';", "")
                .replace("nonce-", "")
        !nonceValue.equals(nonceValue2)

        when:
        String body = rsp.body()
        String body2 = rsp2.body()

        then:
        body
        body2
        rsp.body().contains("<h1>username: <span>sgammon</span></h1>")
        rsp2.body().contains("<h1>username: <span>sgammon</span></h1>")
        rsp.body().contains("nonce=\"${nonceValue}\"")
        rsp2.body().contains("nonce=\"${nonceValue2}\"")
        !rsp.body().contains(nonceValue2)
        !rsp2.body().contains(nonceValue)
    }
}

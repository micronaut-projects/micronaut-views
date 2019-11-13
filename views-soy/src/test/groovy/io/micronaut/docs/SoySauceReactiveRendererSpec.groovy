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
import io.micronaut.views.soy.SoyResponseBuffer
import io.micronaut.views.soy.SoySauceViewsRenderer
import io.micronaut.views.soy.SoyViewsRendererConfigurationProperties
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.util.regex.Pattern


class SoySauceReactiveRendererSpec extends Specification {
    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer,
            [
                    "spec.name": "soy",
                    "micronaut.http.client.read-timeout": "5s",
                    "micronaut.views.soy.enabled": true,
                    "micronaut.views.soy.engine": "sauce",
                    'micronaut.views.thymeleaf.enabled': false,
                    'micronaut.views.velocity.enabled': false,
                    'micronaut.views.handlebars.enabled': false,
                    'micronaut.views.freemarker.enabled': false,
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

    def "invoking /soy/asyncContent should produce a chunked response"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/soy/asyncContent', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        body.contains("<h1>username: <span>sgammon</span></h1>")
        body.contains("Hello async content!")
        rsp.headers.contains("Transfer-Encoding")
        rsp.headers.get("Transfer-Encoding").toLowerCase().contains("chunk")
    }
}

package io.micronaut.docs

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.http.client.exceptions.ReadTimeoutException
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.AutoCleanup
import spock.lang.Issue
import spock.lang.Shared
import spock.lang.Specification

class SoySauceViewRendererBadsyntaxSpec extends Specification {

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer,
    [
            "spec.name": "badsyntax",
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
    HttpClient client = embeddedServer.getApplicationContext().createBean(HttpClient, embeddedServer.getURL())

    @Issue('https://github.com/micronaut-projects/micronaut-views/issues/478')
    def "invoking /soy/badsyntax throws HttpClientException that is not a read timeout"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/badsyntax', String)

        then:
        def e = thrown(HttpClientException)
        !(e instanceof ReadTimeoutException)
    }
}

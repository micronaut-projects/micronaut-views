/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.views.csp

import io.micronaut.context.ApplicationContext
import io.micronaut.context.DefaultApplicationContext
import io.micronaut.context.env.PropertySource
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.server.netty.NettyHttpServer
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.Specification

class CspFilterSpec extends Specification {

    void "test no configuration"() {
        given:
        ApplicationContext applicationContext = new DefaultApplicationContext("test")
        applicationContext.environment.addPropertySource(PropertySource.of("test",
                ['micronaut.views.enabled': false]
        ))
        applicationContext.start()

        expect: "No beans are created"
        !applicationContext.containsBean(CspConfiguration)

        cleanup:
        applicationContext.close()
    }

    void "test csp configuration"() {
        given:
        ApplicationContext applicationContext = new DefaultApplicationContext("test")
        applicationContext.environment.addPropertySource(PropertySource.of("test",
                ['micronaut.views.csp.enabled': true,
                 'micronaut.views.csp.reportOnly': false,
                 'micronaut.views.csp.policyDirectives': "default-src https:"]

        ))
        applicationContext.start()

        expect:
        applicationContext.containsBean(CspConfiguration)

        when:
        CspConfiguration config = applicationContext.getBean(CspConfiguration)

        then:
        config.enabled
        !config.reportOnly
        config.policyDirectives.get() == 'default-src https:'

        cleanup:
        applicationContext.close()
    }

    void "test no CSP configuration"() {
        given:
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, [
                'micronaut.security.enabled': false,
                'spec.name': getClass().simpleName
        ])
        URL server = embeddedServer.getURL()
        HttpClient httpClient = embeddedServer.applicationContext.createBean(HttpClient, server)

        when:
        HttpResponse<?> response = httpClient.toBlocking().exchange(
                HttpRequest.GET('/csp')
        )
        Set<String> headerNames = response.headers.names()

        then:
        response.code() == HttpStatus.OK.code
        !headerNames.contains(CspFilter.CSP_HEADER)
        !headerNames.contains(CspFilter.CSP_REPORT_ONLY_HEADER)

        cleanup:
        embeddedServer.close()
    }

    void "test CSP no response header"() {
        given:
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, [
                'micronaut.security.enabled': false,
                'spec.name': getClass().simpleName
        ])
        URL server = embeddedServer.getURL()
        HttpClient rxClient = embeddedServer.applicationContext.createBean(HttpClient, server)

        when:
        HttpResponse<?>  response = rxClient.toBlocking().exchange(
                HttpRequest.GET('/csp')
        )
        Set<String> headerNames = response.headers.names()

        then:
        response.code() == HttpStatus.OK.code
        !headerNames.contains(CspFilter.CSP_HEADER)
        !headerNames.contains(CspFilter.CSP_REPORT_ONLY_HEADER)

        cleanup:
        embeddedServer.close()
    }

    void "test CSP empty policy directive"() {
        given:
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, [
                'spec.name': getClass().simpleName,
                'micronaut.security.enabled': false,
                'micronaut.views.csp.enabled': true,
                'micronaut.views.csp.reportOnly': false,
                'micronaut.views.csp.policyDirectives': ""
        ])
        URL server = embeddedServer.getURL()
        HttpClient rxClient = embeddedServer.applicationContext.createBean(HttpClient, server)

        when:
        HttpResponse<?> response = rxClient.toBlocking().exchange(
                HttpRequest.GET('/csp')
        )
        Set<String> headerNames = response.headers.names()

        then:
        response.code() == HttpStatus.OK.code
        !headerNames.contains(CspFilter.CSP_HEADER)

        cleanup:
        embeddedServer.close()
    }

    void "test CSP response header"() {
        given:
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, [
                'spec.name': getClass().simpleName,
                'micronaut.security.enabled': false,
                'micronaut.views.csp.enabled': true,
                'micronaut.views.csp.filterPath': "/csp",
                'micronaut.views.csp.reportOnly': false,
                'micronaut.views.csp.policyDirectives': "default-src self:"
        ])
        URL server = embeddedServer.getURL()
        HttpClient rxClient = embeddedServer.applicationContext.createBean(HttpClient, server)

        when:
        HttpResponse<?> response = rxClient.toBlocking().exchange(
                HttpRequest.GET('/csp')
        )
        Set<String> headerNames = response.headers.names()

        then:
        response.code() == HttpStatus.OK.code
        response.header(CspFilter.CSP_HEADER) == "default-src self:"
        !headerNames.contains(CspFilter.CSP_REPORT_ONLY_HEADER)

        cleanup:
        embeddedServer.close()
    }

    void "test CSP response report only header"() {
        given:
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, [
                'spec.name': getClass().simpleName,
                'micronaut.security.enabled': false,
                'micronaut.views.csp.enabled': true,
                'micronaut.views.csp.filterPath': "/csp",
                'micronaut.views.csp.reportOnly': true,
                'micronaut.views.csp.policyDirectives': "default-src self:"
        ])
        URL server = embeddedServer.getURL()
        HttpClient rxClient = embeddedServer.applicationContext.createBean(HttpClient, server)

        when:
        HttpResponse<?> response = rxClient.toBlocking().exchange(
                HttpRequest.GET('/csp')
        )
        Set<String> headerNames = response.headers.names()

        then:
        response.code() == HttpStatus.OK.code
        response.header(CspFilter.CSP_REPORT_ONLY_HEADER) == "default-src self:"
        !headerNames.contains(CspFilter.CSP_HEADER)

        cleanup:
        embeddedServer.close()
    }

    void "test CSP response header ignore"() {
        given:
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, [
                'spec.name': getClass().simpleName,
                'micronaut.security.enabled': false,
                'micronaut.views.csp.enabled': true,
                'micronaut.views.csp.filterPath': "/csp",
                'micronaut.views.csp.reportOnly': false,
                'micronaut.views.csp.policyDirectives': "default-src self:"
        ])
        URL server = embeddedServer.getURL()
        HttpClient rxClient = embeddedServer.applicationContext.createBean(HttpClient, server)

        when:
        HttpResponse<?> response = rxClient.toBlocking().exchange(
                HttpRequest.GET('/csp/ignore')
        )
        Set<String> headerNames = response.headers.names()

        then:
        response.code() == HttpStatus.OK.code
        !headerNames.contains(CspFilter.CSP_HEADER)
        !headerNames.contains(CspFilter.CSP_REPORT_ONLY_HEADER)

        cleanup:
        embeddedServer.close()
    }

    void "test CSP response header nonce injection"() {
        given:
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, [
                'spec.name': getClass().simpleName,
                'micronaut.security.enabled': false,
                'micronaut.views.csp.enabled': true,
                'micronaut.views.csp.generateNonce': true,
                'micronaut.views.csp.filterPath': "/csp",
                'micronaut.views.csp.reportOnly': false,
                'micronaut.views.csp.policyDirectives': "default-src self:; script-src 'nonce-{#nonceValue}';"
        ])
        URL server = embeddedServer.getURL()
        HttpClient rxClient = embeddedServer.applicationContext.createBean(HttpClient, server)

        when:
        HttpResponse<?> response = rxClient.toBlocking().exchange(
                HttpRequest.GET('/csp')
        )
        Set<String> headerNames = response.headers.names()

        then:
        response.code() == HttpStatus.OK.code
        response.header(CspFilter.CSP_HEADER).contains("default-src self:;")
        response.header(CspFilter.CSP_HEADER).contains("'nonce-")
        !headerNames.contains(CspFilter.CSP_REPORT_ONLY_HEADER)

        cleanup:
        embeddedServer.close()
    }

    void "test CSP response header nonce changes every request/response cycle"() {
        given:
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, [
                'spec.name': getClass().simpleName,
                'micronaut.security.enabled': false,
                'micronaut.views.csp.enabled': true,
                'micronaut.views.csp.generateNonce': true,
                'micronaut.views.csp.filterPath': "/csp",
                'micronaut.views.csp.reportOnly': false,
                'micronaut.views.csp.policyDirectives': "default-src self:; script-src 'nonce-{#nonceValue}';"
        ])
        URL server = embeddedServer.getURL()
        HttpClient rxClient = embeddedServer.applicationContext.createBean(HttpClient, server)

        when:
        HttpResponse<?> response1 = rxClient.toBlocking().exchange(
                HttpRequest.GET('/csp')
        )
        Set<String> headerNames1 = response1.headers.names()
        HttpResponse<?> response2 = rxClient.toBlocking().exchange(
                HttpRequest.GET('/csp')
        )
        Set<String> headerNames2 = response2.headers.names()

        then:
        response1.code() == HttpStatus.OK.code
        response1.header(CspFilter.CSP_HEADER).contains("default-src self:;")
        response1.header(CspFilter.CSP_HEADER).contains("'nonce-")
        response2.code() == HttpStatus.OK.code
        response2.header(CspFilter.CSP_HEADER).contains("default-src self:;")
        response2.header(CspFilter.CSP_HEADER).contains("'nonce-")
        !headerNames1.contains(CspFilter.CSP_REPORT_ONLY_HEADER)
        !headerNames2.contains(CspFilter.CSP_REPORT_ONLY_HEADER)
        !response2.header(CspFilter.CSP_HEADER).equals(
                response1.header(CspFilter.CSP_HEADER))

        cleanup:
        embeddedServer.close()
    }

}

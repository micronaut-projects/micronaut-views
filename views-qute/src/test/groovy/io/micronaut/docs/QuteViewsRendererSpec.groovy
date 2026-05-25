/*
 * Copyright 2017-2026 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.docs

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.client.exceptions.ReadTimeoutException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.views.qute.QuteViewsRenderer
import spock.lang.AutoCleanup
import spock.lang.Issue
import spock.lang.Shared
import spock.lang.Specification

class QuteViewsRendererSpec extends Specification {

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer,
            [
                    "spec.name": "qute",
                    "micronaut.security.enabled": false,
            ],
            "test")

    @Shared
    @AutoCleanup
    HttpClient client = embeddedServer.applicationContext.createBean(HttpClient, embeddedServer.URL)

    def "bean is loaded"() {
        when:
        embeddedServer.applicationContext.getBean(QuteViewsRenderer)

        then:
        noExceptionThrown()
    }

    def "invoking /qute/home does not specify @View, thus, regular JSON rendering is used"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange("/qute/home", String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        body.contains("{\"username\":\"sdelamo\",\"loggedIn\":true}")
        rsp.contentType.isPresent()
        rsp.contentType.get() == MediaType.APPLICATION_JSON_TYPE
    }

    def "invoking /qute renders qute template from a controller returning a map"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange("/qute", String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        body.contains("<h1>username: <span>sdelamo</span></h1>")
        rsp.contentType.isPresent()
        rsp.contentType.get() == MediaType.TEXT_HTML_TYPE
    }

    def "invoking /qute/pojo renders qute template from a controller returning a pojo"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange("/qute/pojo", String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        body.contains("<h1>username: <span>sdelamo</span></h1>")
    }

    def "invoking /qute/nullbody renders view even if the response body is null"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange("/qute/nullbody", String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        body.contains("<h1>You are not logged in</h1>")
    }

    def "invoking /qute/bogus returns 500 if you attempt to render a template which does not exist"() {
        when:
        client.toBlocking().exchange("/qute/bogus", String)

        then:
        HttpClientResponseException e = thrown()

        and:
        e.status == HttpStatus.INTERNAL_SERVER_ERROR
    }

    @Issue("https://github.com/micronaut-projects/micronaut-views/issues/478")
    def "invoking /qute/badsyntax throws HttpClientException that is not a read timeout"() {
        when:
        client.toBlocking().exchange("/qute/badsyntax", String)

        then:
        def e = thrown(HttpClientException)
        !(e instanceof ReadTimeoutException)
    }

    def "templates cannot be loaded outside the configured views folder"() {
        when:
        client.toBlocking().exchange("/qute/traversal", String)

        then:
        HttpClientResponseException e = thrown()

        and:
        e.status == HttpStatus.INTERNAL_SERVER_ERROR
    }

    def "html templates escape expression output by default"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange("/qute", String)

        then:
        rsp.body().contains("&lt;admin&gt;")
    }
}

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
package io.micronaut.docs

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.views.ViewsFilter
import io.micronaut.views.pebble.PebbleViewsRenderer
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static io.micronaut.http.HttpHeaders.ACCEPT_LANGUAGE
import static io.micronaut.http.HttpRequest.GET

class PebbleViewsRendererSpec extends Specification {

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer,
            [
                    'spec.name': 'pebble',
                    'micronaut.security.enabled': false,
            ],
            "test")

    @Shared
    @AutoCleanup
    HttpClient client = embeddedServer.getApplicationContext().createBean(HttpClient, embeddedServer.getURL())

    def "bean is loaded"() {
        when:
        embeddedServer.applicationContext.getBean(ViewsFilter)

        then:
        noExceptionThrown()

        when:
        embeddedServer.applicationContext.getBean(PebbleViewsRenderer)

        then:
        noExceptionThrown()
    }

    def "invoking /pebble/home does not specify @View, thus, regular JSON rendering is used"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/pebble/home', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("{\"username\":\"sdelamo\",\"loggedIn\":true}")
        rsp.contentType.isPresent()
        rsp.contentType.get() == MediaType.APPLICATION_JSON_TYPE
    }


    def "invoking /pebble renders pebble template from a controller returning a map"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/pebble', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("<h1>username: <span>sdelamo</span></h1>")
        rsp.contentType.isPresent()
        rsp.contentType.get() == MediaType.TEXT_HTML_TYPE
    }

    def "invoking /pebble/pogo renders pebble template from a controller returning a pogo"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/pebble/pogo', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("<h1>username: <span>sdelamo</span></h1>")
    }

    def "invoking /pebble/bogus returns 404 if you attempt to render a template which does not exist"() {
        when:
        client.toBlocking().exchange('/pebble/bogus', String)

        then:
        HttpClientResponseException e = thrown()

        and:
        e.status == HttpStatus.INTERNAL_SERVER_ERROR
    }

    def "invoking /pebble/nullbody renders view even if the response body is null"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/pebble/nullbody', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("<h1>You are not logged in</h1>")
    }

    def "invoking /text renders pebble text template from a controller returning a map"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/pebble/text', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("username: sdelamo")
    }

    def "invoking /i18n without accept language header"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/pebble/i18n', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("i18n: value-en")
    }

    @Unroll
    def "invoking /i18n renders pebble i18n in different locales (#acceptLanguage)"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange(GET('/pebble/i18n').header(ACCEPT_LANGUAGE, acceptLanguage), String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body() == expectedBody

        where:
        acceptLanguage || expectedBody
        "en"           || "i18n: value-en"
        "en-US"        || "i18n: value-en"
        "fr"           || "i18n: value-en"
        "de"           || "i18n: value-de"
        "de-DE"        || "i18n: value-de"
        "it"           || "i18n: value-it"
    }
}

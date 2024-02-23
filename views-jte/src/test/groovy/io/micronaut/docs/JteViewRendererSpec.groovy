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
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.client.exceptions.ReadTimeoutException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.views.jte.JteViewsRenderer
import spock.lang.AutoCleanup
import spock.lang.Issue
import spock.lang.Shared
import spock.lang.Specification

abstract class JteViewRendererSpec extends Specification {

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer,
            testProperties,
            "test")

    @Shared
    @AutoCleanup
    HttpClient client = embeddedServer.getApplicationContext().createBean(HttpClient, embeddedServer.getURL())

    abstract Map<String, Object> getTestProperties()

    def "bean is loaded"() {
        when:
        List<JteViewsRenderer> jteBeans = embeddedServer.applicationContext.getBeansOfType(JteViewsRenderer)

        then:
        noExceptionThrown()
        !jteBeans.empty
    }

    @Issue('https://github.com/micronaut-projects/micronaut-views/issues/478')
    def "invoking /jte/badsyntax throws HttpClientException that is not a read timeout"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/jte/badsyntax', String)

        then:
        def e = thrown(HttpClientException)
        !(e instanceof ReadTimeoutException)
    }

    def "invoking /jte/home does not specify @View, thus, regular JSON rendering is used"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/jte/home', String)

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

    def "invoking /jte renders jte template from a controller returning a map"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/jte', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("<h1>username: <span>sdelamo</span></h1>")
    }

    def "invoking /jte/pojo renders jte template from a controller returning a pojo"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/jte/pojo', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("<h1>username: <span>sdelamo</span></h1>")
    }

    def "invoking /jte/reactive renders jte template from a controller returning a reactive type"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/jte/reactive', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("<h1>username: <span>sdelamo</span></h1>")
    }

    def "invoking /jte/modelAndView renders jte template from a controller returning a ModelAndView instance"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/jte/modelAndView', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("<h1>username: <span>sdelamo</span></h1>")
    }

    def "invoking /jte/plainText renders with template but sets content type to txt/plain"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange(HttpRequest.GET('/jte/plainText').accept(MediaType.TEXT_PLAIN), String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("<h1>username: <span>sdelamo</span></h1>")
        rsp.getHeaders().getContentType().isPresent()
        rsp.getHeaders().getContentType().get() == MediaType.TEXT_PLAIN
    }

    def "invoking /jte/bogus returns 500 if you attempt to render a template which does not exist"() {
        when:
        client.toBlocking().exchange('/jte/bogus', String)

        then:
        HttpClientResponseException e = thrown()

        and:
        e.status == HttpStatus.INTERNAL_SERVER_ERROR
    }

    def "invoking /jte/nullbody renders view even if the response body is null"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/jte/nullbody', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("<h1>You are not logged in</h1>")
    }

    def "invoking /jte/kte renders a kotlin view"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/jte/kte', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("<h1>username: <span>sdelamo</span></h1>")
    }
}

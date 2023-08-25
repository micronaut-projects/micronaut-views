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
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.client.exceptions.ReadTimeoutException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.views.ViewsFilter
import io.micronaut.views.jstachio.JStachioViewsRenderer
import spock.lang.AutoCleanup
import spock.lang.Issue
import spock.lang.Shared
import spock.lang.Specification

class JStachioViewsRendererSpec extends Specification {

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer,
            [
                    'spec.name': 'jstachio',
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
        embeddedServer.applicationContext.getBean(JStachioViewsRenderer)

        then:
        noExceptionThrown()
    }


    def "invoking /jstachio/home does not specify @View, thus, regular JSON rendering is used"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/jstachio/home', String)

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
    
    def "invoking /jstachio/bodyWriter uses produces text/html"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/jstachio/bodyWriter', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("<h1>username: <span>sdelamo body writer</span></h1>")
        rsp.contentType.isPresent()
        rsp.contentType.get().getCharset().get().toString() == "UTF-8"
        rsp.contentType.get().name == MediaType.TEXT_HTML
        
        rsp.contentLength == 140
    }



    def "invoking /jstachio/pojo renders jstachio template from a controller returning a pojo"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/jstachio/pojo', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("<h1>username: <span>sdelamo</span></h1>")
    }
    
    def "invoking /jstachio/modelAndView renders jstachio template from a controller returning a ModelAndView instance"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/jstachio/modelAndView', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("<h1>username: <span>sdelamo</span></h1>")
    }
    
    def "invoking /jstachio/static renders jstachio template from a controller returning a static template"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/jstachio/static', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("<h1>username: <span>sdelamo</span></h1>")
    }

    def "invoking /jstachio/bogus returns 404 if you attempt to render a template which does not exist"() {
        when:
        client.toBlocking().exchange('/jstachio/bogus', String)

        then:
        HttpClientResponseException e = thrown()

        and:
        e.status == HttpStatus.INTERNAL_SERVER_ERROR
    }

    // This varies considerable from other templating engines. We require a model to be not null.
    def "invoking /jstachio/nullbody WILL NOT render if response body is null"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/jstachio/nullbody', String)

        then:
        HttpClientResponseException e = thrown()
        
        and:
        e.status == HttpStatus.INTERNAL_SERVER_ERROR
    }
}

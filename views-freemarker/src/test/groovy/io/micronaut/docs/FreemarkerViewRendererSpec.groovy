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
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.views.ViewsFilter
import io.micronaut.views.freemarker.FreemarkerViewsRenderer
import io.micronaut.views.freemarker.FreemarkerViewsRendererConfigurationProperties
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

class FreemarkerViewRendererSpec extends Specification {

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer,
            [
                    'spec.name': 'freemarker',
                    'micronaut.security.enabled': false,
                    'micronaut.views.freemarker.lazy-imports': true,
                    'micronaut.views.freemarker.settings.urlEscapingCharset': 'UTF-8'
            ],
            "test")

    @Shared
    @AutoCleanup
    RxHttpClient client = embeddedServer.getApplicationContext().createBean(RxHttpClient, embeddedServer.getURL())

    def "bean is loaded"() {
        when:
        embeddedServer.applicationContext.getBean(FreemarkerViewsRenderer)
        embeddedServer.applicationContext.getBean(ViewsFilter)

        then:
        noExceptionThrown()

        when:
        FreemarkerViewsRendererConfigurationProperties props = embeddedServer.applicationContext.getBean(FreemarkerViewsRendererConfigurationProperties)

        then:
        !props.isCacheStorageExplicitlySet()
        props.lazyImports
        props.URLEscapingCharset == 'UTF-8'
    }

    def "invoking /freemarker/home does not specify @View, thus, regular JSON rendering is used"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/freemarker/home', String)

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

    def "invoking /freemarker renders freemarker template from a controller returning a map"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/freemarker', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("<h1>username: <span>sdelamo</span></h1>")
    }

    def "invoking /freemarker/pogo renders freemarker template from a controller returning a pogo"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/freemarker/pogo', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("<h1>username: <span>sdelamo</span></h1>")
    }

    def "invoking /freemarker/reactive renders freemarker template from a controller returning a reactive type"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/freemarker/reactive', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("<h1>username: <span>sdelamo</span></h1>")
    }

    def "invoking /freemarker/modelAndView renders freemarker template from a controller returning a ModelAndView instance"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/freemarker/modelAndView', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("<h1>username: <span>sdelamo</span></h1>")
    }

    def "invoking /freemarker/viewWithNoViewRendererForProduces skips view rendering since controller specifies a media type with no available ViewRenderer"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/freemarker/viewWithNoViewRendererForProduces', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then: 'default JSON rendering is used since no bean ViewRenderer is available for text/csv media type'
        body
        rsp.body() == 'io.micronaut.docs.Person(sdelamo, true)'
    }

    def "invoking /freemarker/bogus returns 500 if you attempt to render a template which does not exist"() {
        when:
        client.toBlocking().exchange('/freemarker/bogus', String)

        then:
        def e = thrown(HttpClientResponseException)

        and:
        e.status == HttpStatus.INTERNAL_SERVER_ERROR
        e.message == "Internal Server Error: View [bogus] does not exist"
    }

    def "invoking /freemarker/nullbody renders view even if the response body is null"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/freemarker/nullbody', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("<h1>You are not logged in</h1>")
    }

    def "invoking /freemarker/invalid returns error"() {
        when:
        client.toBlocking().exchange('/freemarker/invalid', String)

        then:
        def e = thrown(HttpClientResponseException)

        and:
        e.status == HttpStatus.INTERNAL_SERVER_ERROR
    }
}

/*
 * Copyright 2017-2021 original authors
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
package views

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.views.model.FruitsController
import spock.lang.Specification

class ModelAndViewSpec extends Specification {

    def "a view model can be any object"() {
        given:
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, [
                'spec.name'                                       : 'ModelAndViewSpec',
                'micronaut.views.soy.enabled'                     : false,
                'micronaut.security.views-model-decorator.enabled': false,
        ]) as EmbeddedServer
        HttpClient httpClient = HttpClient.create(embeddedServer.URL)

        expect:
        embeddedServer.applicationContext.containsBean(FruitsController)

        when:
        HttpRequest request = HttpRequest.GET("/")
        HttpResponse<String> response = httpClient.toBlocking().exchange(request, String)

        then:
        response.status() == HttpStatus.OK

        when:
        String html = response.body()

        then:
        html

        and:
        html.contains('<h1>fruit: apple</h1>')

        and:
        html.contains('<h1>color: red</h1>')

        cleanup:
        httpClient.close()

        and:
        embeddedServer.close()
    }

    def "returning a null model causes a 404"() {
        given:
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, [
                'spec.name'                                       : 'ModelAndViewSpec',
                'micronaut.views.soy.enabled'                     : false,
                'micronaut.security.views-model-decorator.enabled': false,
        ]) as EmbeddedServer
        HttpClient httpClient = HttpClient.create(embeddedServer.URL, )

        expect:
        embeddedServer.applicationContext.containsBean(FruitsController)

        when:
        HttpRequest request = HttpRequest.GET("/null")
        httpClient.toBlocking().exchange(request, String)

        then:
        thrown(HttpClientResponseException)

        cleanup:
        httpClient.close()

        and:
        embeddedServer.close()
    }

    def "a view model can be a map"() {
        given:
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, [
                'spec.name'                                       : 'ModelAndViewSpec',
                'micronaut.views.soy.enabled'                     : false,
                'micronaut.security.views-model-decorator.enabled': false,
        ]) as EmbeddedServer
        HttpClient httpClient = HttpClient.create(embeddedServer.URL)

        expect:
        embeddedServer.applicationContext.containsBean(FruitsController)

        when:
        HttpRequest request = HttpRequest.GET("/map")
        HttpResponse<String> response = httpClient.toBlocking().exchange(request, String)

        then:
        response.status() == HttpStatus.OK

        when:
        String html = response.body()
        println "=" * 100
        println "html -> " + html
        println "=" * 100

        then:
        html

        and:
        html.contains('<h1>fruit: orange</h1>')

        and:
        html.contains('<h1>color: orange</h1>')

        cleanup:
        httpClient.close()

        and:
        embeddedServer.close()
    }
}

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
import io.micronaut.views.model.ConfigViewModelProcessor
import io.micronaut.views.model.FruitsController
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

class ModelAndViewSpec extends Specification {
    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, [
            'spec.name'                                       : 'ModelAndViewSpec',
            'micronaut.views.soy.enabled'                     : false,
            'micronaut.security.views-model-decorator.enabled': false,
            'micronaut.application.name'                      : 'test'
    ]) as EmbeddedServer

    @AutoCleanup
    @Shared
    HttpClient httpClient = embeddedServer.applicationContext.createBean(HttpClient, embeddedServer.URL)

    def "a view model can be any object"() {
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
    }

    def "returning a null model causes a 404"() {
        expect:
        embeddedServer.applicationContext.containsBean(FruitsController)

        when:
        HttpRequest request = HttpRequest.GET("/null")
        httpClient.toBlocking().exchange(request, String)

        then:
        thrown(HttpClientResponseException)
    }

    def "a view model can be a map"() {
        expect:
        embeddedServer.applicationContext.containsBean(FruitsController)

        when:
        HttpRequest request = HttpRequest.GET("/map")
        HttpResponse<String> response = httpClient.toBlocking().exchange(request, String)

        then:
        response.status() == HttpStatus.OK

        when:
        String html = response.body()

        then:
        html

        and:
        html.contains('<h1>fruit: orange</h1>')

        and:
        html.contains('<h1>color: orange</h1>')
    }

    def "models can be dynamically enhanced"() {
        expect:
        embeddedServer.applicationContext.containsBean(FruitsController)

        when:
        HttpRequest request = HttpRequest.GET("/processor")
        HttpResponse<String> response = httpClient.toBlocking().exchange(request, String)

        then:
        response.status() == HttpStatus.OK

        when:
        String html = response.body()

        then:
        html

        and:
        embeddedServer.applicationContext.containsBean(ConfigViewModelProcessor.class)

        and:
        html.contains('<h1>config: test</h1>')
    }
}

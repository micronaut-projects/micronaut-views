/*
 * Copyright 2017-2020 original authors
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
package io.micronaut.views.thymeleaf.webexpression

import io.micronaut.http.HttpRequest
import spock.lang.Specification

class RequestExpressionObjectSpec extends Specification {

    void "exposes metadata from a relative request URI"() {
        given:
        def expressionObject = new RequestExpressionObject(HttpRequest.GET('/views/request?name=Tim'), null, '/demo')

        expect:
        expressionObject.contextPath == '/demo'
        expressionObject.method == 'GET'
        expressionObject.path == '/views/request'
        expressionObject.requestURI == '/views/request'
        expressionObject.requestURL == '/views/request'
        expressionObject.queryString == 'name=Tim'
    }

    void "returns empty context path when none is configured"() {
        expect:
        new RequestExpressionObject(HttpRequest.GET('/views/request'), null, null).contextPath == ''
    }

    void "uses the absolute URI authority for request URL"() {
        given:
        def expressionObject = new RequestExpressionObject(HttpRequest.GET('https://example.com:8443/views/request?name=Tim'), null, null)

        expect:
        expressionObject.requestURL == 'https://example.com:8443/views/request'
        expressionObject.queryString == 'name=Tim'
    }

    void "builds request URL from server address when absolute URI has no authority"() {
        given:
        HttpRequest<?> request = Stub(HttpRequest) {
            getUri() >> URI.create('https:/views/request?name=Tim')
            getServerName() >> 'example.com'
            getServerAddress() >> new InetSocketAddress('example.com', 443)
        }

        expect:
        new RequestExpressionObject(request, null, null).requestURL == 'https://example.com:443/views/request'
    }
}

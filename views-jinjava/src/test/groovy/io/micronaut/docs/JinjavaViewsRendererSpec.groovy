package io.micronaut.docs

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.views.jinjava.JinjavaViewsRenderer
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

class JinjavaViewsRendererSpec extends Specification {

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, [
        'spec.name': 'jinjava',
        'micronaut.security.enabled': false,
    ], 'test')

    @Shared
    @AutoCleanup
    HttpClient client = embeddedServer.applicationContext.createBean(HttpClient, embeddedServer.URL)

    void "bean is loaded"() {
        expect:
        embeddedServer.applicationContext.getBean(JinjavaViewsRenderer)
    }

    void "renders a model and a POJO with Jinja syntax"() {
        expect:
        client.toBlocking().retrieve('/jinjava').contains('<h1>username: <span>sdelamo</span></h1>')
        client.toBlocking().retrieve('/jinjava/pojo').contains('<h1>username: <span>sdelamo</span></h1>')
    }

    void "renders included and inherited templates"() {
        expect:
        client.toBlocking().retrieve('/jinjava').contains('<title>Jinjava Home</title>')
    }

    void "a missing view fails rendering"() {
        when:
        client.toBlocking().retrieve('/jinjava/bogus')

        then:
        def error = thrown(HttpClientResponseException)
        error.status == HttpStatus.INTERNAL_SERVER_ERROR
    }

    void "an invalid view fails rendering"() {
        when:
        client.toBlocking().retrieve('/jinjava/badsyntax')

        then:
        thrown(HttpClientException)
    }
}

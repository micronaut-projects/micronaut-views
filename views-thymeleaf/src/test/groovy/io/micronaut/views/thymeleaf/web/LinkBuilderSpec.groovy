package io.micronaut.views.thymeleaf.web

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.Specification

class LinkBuilderSpec extends Specification {

    void "test context path prepended to context relative url"() {
        given:
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, [
                "spec.name"                        : "LinkBuilderSpec",
                "micronaut.views.thymeleaf.enabled": true,
                "micronaut.server.context-path"    : "/context",
        ], "test")
        HttpClient client = embeddedServer.getApplicationContext().createBean(HttpClient, embeddedServer.getURL())

        when:
        HttpResponse<String> response = client.toBlocking().exchange(HttpRequest.GET("/context/link"), String)

        then:
        response.status() == HttpStatus.OK
        response.body().contains("href=\"/context/other\"")

        cleanup:
        embeddedServer.close()
    }

    void "test no context path"() {
        given:
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, [
                "spec.name"                        : "LinkBuilderSpec",
                "micronaut.views.thymeleaf.enabled": true,
        ], "test")
        HttpClient client = embeddedServer.getApplicationContext().createBean(HttpClient, embeddedServer.getURL())

        when:
        HttpResponse<String> response = client.toBlocking().exchange(HttpRequest.GET("/link"), String)

        then:
        response.status() == HttpStatus.OK
        response.body().contains("href=\"/other\"")

        cleanup:
        embeddedServer.close()
    }
}

package io.micronaut.views

import io.micronaut.context.ApplicationContext
import io.micronaut.core.util.CollectionUtils
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.views.model.UnmodifiableFruitsController
import spock.lang.Specification

// Issue: https://github.com/micronaut-projects/micronaut-views/issues/336
class UnmodifiableModelAndViewSpec extends Specification {

    void 'an unmodifiable ViewModel reports error but does not crash'() {
        given:
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class, [
                "spec.name"                  : "UnmodifiableModelAndViewSpec",
                "micronaut.views.soy.enabled": StringUtils.FALSE
        ])
        HttpClient httpClient = HttpClient.create(embeddedServer.getURL())
        BlockingHttpClient client = httpClient.toBlocking()

        expect:
        embeddedServer.applicationContext.containsBean(UnmodifiableFruitsController.class)

        when:
        HttpRequest<?> request = HttpRequest.GET("/unmodifiable").basicAuth("john", "secret")
        HttpResponse<String> response = client.exchange(request, String.class)

        then:
        response.status() == HttpStatus.OK

        when:
        String html = response.body()

        then:
        html != null

        and:
        html.contains("<blink>Security was added</blink>")

        and:
        html.contains("<h1>fruit: plum</h1>")

        and:
        html.contains("<h1>color: plum</h1>")

        cleanup:
        httpClient.close()
        embeddedServer.close()
    }

    void 'a modifiable ViewModel still adds security'() {
        given:
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class, [
                "spec.name"                  : "UnmodifiableModelAndViewSpec",
                "micronaut.views.soy.enabled": StringUtils.FALSE
        ])
        HttpClient httpClient = HttpClient.create(embeddedServer.getURL())
        BlockingHttpClient client = httpClient.toBlocking()

        expect:
        embeddedServer.applicationContext.containsBean(UnmodifiableFruitsController)

        when:
        HttpRequest<?> request = HttpRequest.GET("/modifiable").basicAuth("john", "secret")
        HttpResponse<String> response = client.exchange(request, String.class)

        then:
        response.status() == HttpStatus.OK

        when:
        String html = response.body()

        then:
        html != null

        and:
        html.contains("<blink>Security was added</blink>")

        and:
        html.contains("<h1>fruit: plum</h1>")

        and:
        html.contains("<h1>color: plum</h1>")

        cleanup:
        httpClient.close()
        embeddedServer.close()
    }
}

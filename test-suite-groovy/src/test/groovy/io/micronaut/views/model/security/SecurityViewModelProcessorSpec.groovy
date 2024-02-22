package io.micronaut.views.model.security

import io.micronaut.context.ApplicationContext
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.Specification

import java.util.Map

class SecurityViewModelProcessorSpec extends Specification {

    void 'if micronaut security ViewModelDecorator enabled true SecurityViewsModelDecorator bean exists'() {
        given:
        ApplicationContext applicationContext = ApplicationContext.run(
                "micronaut.views.soy.enabled": StringUtils.FALSE,
                "micronaut.security.views-model-decorator.enabled": StringUtils.FALSE
        )

        expect:
        !applicationContext.containsBean(SecurityViewModelProcessor.class)

        cleanup:
        applicationContext.close()
    }

    void 'by default SecurityViewsModelDecorator bean exists'() {
        given:
        ApplicationContext applicationContext = ApplicationContext.run(
                "micronaut.views.soy.enabled": StringUtils.FALSE
        )

        expect:
        applicationContext.containsBean(SecurityViewModelProcessor.class)

        cleanup:
        applicationContext.close()
    }

    void 'a custom SecurityPropertyName can be injected to the model'() {
        given:
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class, [
                "spec.name"                                            : "SecurityViewModelProcessorSpec",
                "micronaut.views.soy.enabled"                          : StringUtils.FALSE,
                "micronaut.security.views-model-decorator.security-key": "securitycustom"
        ])
        HttpClient httpClient = HttpClient.create(embeddedServer.getURL())

        expect:
        embeddedServer.getApplicationContext().containsBean(BooksController.class)

        and:
        embeddedServer.getApplicationContext().containsBean(MockAuthenticationProvider.class)

        and:
        embeddedServer.getApplicationContext().containsBean(SecurityViewModelProcessor.class)

        when:
        HttpRequest<?> request = HttpRequest.GET("/").basicAuth("john", "secret")
        HttpResponse<String> response = httpClient.toBlocking().exchange(request, String.class)

        then:
        response.status() == HttpStatus.OK

        when:
        String html = response.body()

        then:
        html != null

        and:
        !html.contains("User: john")

        and:
        html.contains("Custom: john")

        cleanup:
        httpClient.close()
        embeddedServer.close()
    }

    void 'security property is injected to the model'() {
        given:
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class, [
                "spec.name"                  : "SecurityViewModelProcessorSpec",
                "micronaut.views.soy.enabled": StringUtils.FALSE
        ])

        HttpClient httpClient = HttpClient.create(embeddedServer.getURL())

        expect:
        embeddedServer.applicationContext.containsBean(BooksController.class)

        and:
        embeddedServer.applicationContext.containsBean(MockAuthenticationProvider.class)

        and:
        embeddedServer.applicationContext.containsBean(SecurityViewModelProcessor.class)

        when:
        HttpRequest<?> request = HttpRequest.GET("/").basicAuth("john", "secret")
        HttpResponse<String> response = httpClient.toBlocking().exchange(request, String.class)

        then:
        response.status() == HttpStatus.OK

        when:
        String html = response.body()

        then:
        html != null
        html.contains("User: john email: john@email.com")

        and:
        html.contains("Developing Microservices")

        cleanup:
        httpClient.close()
        embeddedServer.close()
    }
}

package views

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Requires
import io.micronaut.core.util.StringUtils
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.views.View
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

class MultipleViewRendererSpec extends Specification {
    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, [
            "micronaut.security.enabled": false,
            'spec.name': 'MultipleViewRendererSpec',
            'micronaut.views.soy.enabled': false,
    ]) as EmbeddedServer

    @Shared
    @AutoCleanup
    HttpClient httpClient = embeddedServer.applicationContext.createBean(HttpClient, embeddedServer.URL)

    @Shared
    BlockingHttpClient client = httpClient.toBlocking()

    void "multiple ViewRenderer are possible"() {
        when:
        String html = client.retrieve('/stark', String)

        then:
        html.contains('John Snow')

        when:
        html = client.retrieve('/targaryen', String)

        then:
        html.contains('Aegon Targaryen')
    }

    @Requires(property = 'spec.name', value = 'MultipleViewRendererSpec')
    @Controller
    static class GotController {
        @View('stark')
        @Get('/stark')
        Map<String, Object> stark() {
            new HashMap<String, Object>();
        }

        @View('targaryen')
        @Get('/targaryen')
        Map<String, Object> targaryen() {
            new HashMap<String, Object>();
        }
    }
}


package io.micronaut.docs

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification


class DynamicRendererSpec extends Specification {
    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer,
            [
                    'spec.name': 'jte',
                    'micronaut.security.enabled': false,
                    'micronaut.views.jte.dynamic': true,
            'micronaut.views.jte.dynamicSourcePath': 'src/test/jte'],
            "test")

    def sourceFile = new File("src/test/jte/dynamic.jte")

    @Shared
    @AutoCleanup
    HttpClient client = embeddedServer.getApplicationContext().createBean(HttpClient, embeddedServer.getURL())

    def 'invoking /jte/hello returns a page'() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/jte/hello', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("world")
    }

    def 'after the source is modified, the page content changes'() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/jte/hello', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("Hello")

        when:
        modifySource()
        rsp = client.toBlocking().exchange('/jte/hello', String)
        body = rsp.body()

        then:
        body
        rsp.body().contains("Goodbye")

        cleanup:
        revertSource()
    }

    void modifySource() {
        sourceFile.text = sourceFile.text.replaceAll("Hello", "Goodbye")
    }

    void revertSource() {
        sourceFile.text = sourceFile.text.replaceAll("Goodbye", "Hello")
    }
}

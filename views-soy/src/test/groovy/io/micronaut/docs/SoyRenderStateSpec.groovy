package io.micronaut.docs

import io.micronaut.context.ApplicationContext
import io.micronaut.core.io.Writable
import io.micronaut.http.client.RxHttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.views.ViewsFilter
import io.micronaut.views.soy.SoyRender
import io.micronaut.views.soy.SoySauceViewsRenderer
import io.micronaut.views.soy.SoyViewsRendererConfigurationProperties
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import java.nio.charset.StandardCharsets


/**
 * Tests the SoyRender class
 */
class SoyRenderStateSpec extends Specification {
    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer,
            [
                    "spec.name": "soy",
                    "micronaut.views.soy.enabled": true,
                    "micronaut.views.soy.engine": "sauce",
                    'micronaut.views.thymeleaf.enabled': false,
                    'micronaut.views.velocity.enabled': false,
                    'micronaut.views.handlebars.enabled': false,
                    'micronaut.views.freemarker.enabled': false,
                    'micronaut.views.csp.enabled': true,
                    'micronaut.views.csp.generateNonce': true,
                    'micronaut.views.csp.reportOnly': false,
                    'micronaut.views.csp.policyDirectives': "default-src self:; script-src 'nonce-{#nonceValue}';"
            ],
            "test")

    @Shared
    @AutoCleanup
    RxHttpClient client = embeddedServer.getApplicationContext().createBean(RxHttpClient, embeddedServer.getURL())

    def "bean is loaded"() {
        when:
        embeddedServer.applicationContext.getBean(SoySauceViewsRenderer)
        embeddedServer.applicationContext.getBean(ViewsFilter)

        then:
        noExceptionThrown()

        when:
        SoyViewsRendererConfigurationProperties props = embeddedServer.applicationContext.getBean(
                SoyViewsRendererConfigurationProperties)

        then:
        props.isEnabled()
    }

    def "SoyRender should work as an Appendable and a Writable"() {
        when:
        SoyRender obj = SoyRender.create()
        Appendable objAsAppendable = obj
        Writable objAsWritable = obj
        obj.append("hello 123")
        objAsAppendable.append("456789", 3, 5)
        objAsAppendable.append("0".toCharArray()[0])

        then:
        noExceptionThrown()

        when:
        OutputStream outputStream = new ByteArrayOutputStream()
        objAsWritable.writeTo(outputStream, StandardCharsets.UTF_8)
        String encoded = new String(outputStream.toByteArray(), StandardCharsets.UTF_8)

        then:
        encoded == "hello 123780"
    }
}

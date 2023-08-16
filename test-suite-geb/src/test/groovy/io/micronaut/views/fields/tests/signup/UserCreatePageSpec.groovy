package io.micronaut.views.fields.tests.signup

import geb.Browser
import geb.spock.GebSpec
import io.micronaut.context.annotation.Property
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.views.fields.BaseUrlUtils
import jakarta.inject.Inject


@Property(name = "micronaut.resources.html.theme", value = "bootstrap")
@Property(name = "datasources.default.username", value = "sa")
@Property(name = "datasources.default.password", value = "")
@Property(name = "datasources.default.driver-class-name", value = "org.h2.Driver")
@Property(name = "datasources.default.dialect", value = "H2")
@Property(name = "datasources.default.schema-generate", value = "CREATE_DROP")
@Property(name = "datasources.default.url", value = "jdbc:h2:mem:locationDb;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE")
@Property(name = "spec.name", value = "UserCreatePageSpec")
@MicronautTest
class UserCreatePageSpec extends GebSpec {
    @Inject
    EmbeddedServer embeddedServer

    @Override
    Browser getBrowser() {
        Browser browser = super.getBrowser()
        if (embeddedServer) {
            browser.baseUrl = BaseUrlUtils.getBaseUrl(embeddedServer)
        }
        browser
    }

    void "password validation"() {
        given:
        String name = "Huerta del Obispo"

        when:
        browser.to(UserCreatePage)

        then:
        browser.at(UserCreatePage)

        when:
        UserCreatePage page = browser.page(UserCreatePage)

        then:
        !page.hasGlobalErrors()

        when:

        page.save("sdelamo", "foo", "bar")

        then:
        browser.at(UserCreatePage)

        when:
        page = browser.page(UserCreatePage)

        then:
        page.hasGlobalErrors()
    }
}

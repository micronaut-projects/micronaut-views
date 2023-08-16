package io.micronaut.views.fields.tests.location

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
@Property(name = "spec.name", value = "LocationCreateSpec")
@MicronautTest
class LocationCreateSpec extends GebSpec {
    @Inject
    LocationRepository locationRepository

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

    void "location crud"() {
        given:
        String name = "Huerta del Obispo"

        when:
        browser.to(LocationCreatePage)

        then:
        browser.at(LocationCreatePage)

        when:
        LocationCreatePage page = browser.page(LocationCreatePage)
        page.save(name, null, null)

        then:
        noExceptionThrown()
        old(locationRepository.count()) + 1 == locationRepository.count()

        when:
        Location location = locationByName(name)
        browser.to(LocationEditPage, location.id)

        then:
        browser.at(LocationEditPage)

        when:
        LocationEditPage editPage = browser.page(LocationEditPage)
        editPage.update(name, 91f, 181f)

        then:
        browser.at(LocationEditPage)

        when:
        Float latitude = 40.481722f
        Float longitude = -3.372111f
        editPage = browser.page(LocationEditPage)
        editPage.update(name, latitude, longitude)

        then:
        old(locationRepository.count()) == locationRepository.count()

        when:
        location = locationByName(name)

        then:
        new Location(id: location.id, name: name, latitude: latitude, longitude: longitude) == location

        when:
        browser.to(LocationCreatePage)

        then:
        browser.at(LocationCreatePage)

        when:
        page = browser.page(LocationCreatePage)
        page.save(name, 91, 91)

        then:
        browser.at(LocationCreatePage)

        cleanup:
        locationRepository.deleteAll()
    }

    Location locationByName(String author) {
        locationRepository.findAll().find { it.name == author }
    }
}

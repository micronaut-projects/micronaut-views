package io.micronaut.views.fields.tests

import geb.Browser
import geb.spock.GebSpec
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.NonNull
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.views.fields.BaseUrlUtils
import io.micronaut.views.fields.controllers.createsave.CreateHtmlResource
import io.micronaut.views.fields.controllers.createsave.SaveService
import io.micronaut.views.fields.forms.Contact
import io.micronaut.views.fields.pages.ContactCreatePage
import io.micronaut.views.fields.pages.AuthorShowPage
import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Singleton
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.testcontainers.DockerClientFactory
import spock.lang.Shared

@Property(name = "spec.name", value = "ContactCreateSpec")
@spock.lang.Requires({ DockerClientFactory.instance().isDockerAvailable() })
@MicronautTest
class ContactCreateSpec extends GebSpec {

    @Shared
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

    void "contact create form is created"() {
        when:
        browser.to(ContactCreatePage)

        then:
        browser.at(ContactCreatePage)

        when:
        ContactCreatePage contactCreatePage = browser.page(ContactCreatePage)
        contactCreatePage.save("Sergio", "del Amo", "delamos@unityfoundation.io")

        then:
        browser.at(AuthorShowPage)
    }

    @Requires(property = "spec.name", value = "ContactCreateSpec")
    @Controller("/contact")
    static class ContactListController {
        @Produces(MediaType.TEXT_HTML)
        @Get("/list")
        String list() {
            "<!DOCTYPE html><html><head><title>List Contact</title></head><body></body></html>"
        }
    }

    @Requires(property = "spec.name", value = "ContactCreateSpec")
    @Named("contact")
    @Singleton
    static class ContactResources implements CreateHtmlResource<Contact> {
        @Override
        Class<Contact> createClass() {
            Contact
        }
    }

    @Requires(property = "spec.name", value = "ContactCreateSpec")
    @Named("contact")
    @Singleton
    static class ContactSaveService implements SaveService<Contact> {
        @Override
        URI save(@NonNull @NotNull @Valid Contact form) {
            URI.create("/contact/list");
        }
    }
}

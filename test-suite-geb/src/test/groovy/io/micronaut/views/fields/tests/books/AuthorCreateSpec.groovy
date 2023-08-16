package io.micronaut.views.fields.tests.books

import geb.Browser
import geb.spock.GebSpec
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
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
@Property(name = "datasources.default.url", value = "jdbc:h2:mem:devDb;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE")
@Property(name = "spec.name", value = "AuthorCreateSpec")
@MicronautTest
class AuthorCreateSpec extends GebSpec {
    @Inject
    AuthorRepository authorRepository

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

    void "author crud"() {
        when:
        browser.to(AuthorCreatePage)

        then:
        browser.at(AuthorCreatePage)

        when:
        String author = "Sam"
        AuthorCreatePage createPage = browser.page(AuthorCreatePage)
        createPage.save(author)

        then:
        browser.at(AuthorShowPage)
        old(authorRepository.count()) + 1 == authorRepository.count()

        when:
        AuthorEntity authorEntity = authorByName(authorRepository, author)
        browser.to(AuthorEditPage, authorEntity.id)

        then:
        browser.at(AuthorEditPage)

        when:
        AuthorEditPage authorEditPage = browser.page(AuthorEditPage)
        author = "Sam Newman"
        authorEditPage.update(author)

        then:
        browser.at(AuthorShowPage)
        old(authorRepository.count()) + 0 == authorRepository.count()
        author == authorByName(authorRepository, author)?.name


        cleanup:
        authorRepository.deleteAll()
    }

    AuthorEntity authorByName(AuthorRepository authorRepository, String author) {
        authorRepository.findAll().find { it.name == author }
    }
}

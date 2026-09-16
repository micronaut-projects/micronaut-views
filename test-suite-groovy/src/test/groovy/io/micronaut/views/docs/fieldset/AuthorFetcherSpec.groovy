package io.micronaut.views.docs.fieldset

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.views.fields.Form
import io.micronaut.views.fields.FormGenerator
import io.micronaut.views.fields.elements.InputHiddenFormElement
import io.micronaut.views.fields.elements.SelectFormElement
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest(startApplication = false)
class AuthorFetcherSpec extends Specification {

    @Inject
    FormGenerator formGenerator

    void "the options of the select are loaded with the fetcher"() {
        when:
        Form form = formGenerator.generate("/books/authors/save", BookAuthorSave)

        then:
        form.action() == "/books/authors/save"
        form.fieldset().fields()[0] instanceof InputHiddenFormElement
        form.fieldset().fields()[0].name() == "bookId"
        form.fieldset().fields()[1] instanceof SelectFormElement
        form.fieldset().fields()[1].name() == "authorId"
        form.fieldset().fields()[1].options()*.value() == ["1", "2", "3"]
        form.fieldset().fields()[1].options()*.label()*.defaultMessage() == ["Kishori Sharan", "Peter Späth", "Sam Newman"]
        form.fieldset().fields()[1].options().every { !it.selected() }

        when:
        form = formGenerator.generate("/books/authors/save", new BookAuthorSave(1L, 3L))

        then:
        form.fieldset().fields()[1].options()*.selected() == [false, false, true]
    }
}

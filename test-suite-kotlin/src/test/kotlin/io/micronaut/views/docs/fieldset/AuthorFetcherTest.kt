package io.micronaut.views.docs.fieldset

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.views.fields.FormGenerator
import io.micronaut.views.fields.elements.InputHiddenFormElement
import io.micronaut.views.fields.elements.Option
import io.micronaut.views.fields.elements.SelectFormElement
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test

@MicronautTest(startApplication = false)
internal class AuthorFetcherTest {

    @Inject
    lateinit var formGenerator: FormGenerator

    @Test
    fun theOptionsOfTheSelectAreLoadedWithTheFetcher() {
        var form = formGenerator.generate("/books/authors/save", BookAuthorSave::class.java)
        assertEquals("/books/authors/save", form.action())
        val bookId = assertInstanceOf(InputHiddenFormElement::class.java, form.fieldset().fields()[0])
        assertEquals("bookId", bookId.name())
        var authorId = assertInstanceOf(SelectFormElement::class.java, form.fieldset().fields()[1])
        assertEquals("authorId", authorId.name())
        val options = authorId.options()
        assertEquals(listOf("1", "2", "3"), options.map(Option::value))
        assertEquals(listOf("Kishori Sharan", "Peter Späth", "Sam Newman"), options.map { it.label().defaultMessage() })
        assertFalse(options.any(Option::selected))

        form = formGenerator.generate("/books/authors/save", BookAuthorSave(1L, 3L))
        authorId = assertInstanceOf(SelectFormElement::class.java, form.fieldset().fields()[1])
        assertEquals(listOf(false, false, true), authorId.options().map(Option::selected))
    }
}

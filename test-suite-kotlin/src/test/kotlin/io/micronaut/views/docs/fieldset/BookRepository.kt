package io.micronaut.views.docs.fieldset

import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import java.util.Collections

@Requires(property = "spec.name", value = "BookControllerTest")
@Singleton
class BookRepository {
    private val books: MutableList<Book> = Collections.synchronizedList(mutableListOf())

    fun findAll(): List<Book> = books.toList()

    fun save(book: Book): Book {
        books.add(book)
        return book
    }
}

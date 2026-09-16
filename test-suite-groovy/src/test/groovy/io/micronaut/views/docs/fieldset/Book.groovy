package io.micronaut.views.docs.fieldset

import io.micronaut.core.annotation.Introspected

@Introspected
class Book {
    final String title
    final Integer pages

    Book(String title, Integer pages) {
        this.title = title
        this.pages = pages
    }
}

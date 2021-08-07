package io.micronaut.views.model

class Library {
    private final List<Book> books = new ArrayList<>()

    List<Book> getBooks() {
        return books
    }
}

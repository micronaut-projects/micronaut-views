from jakarta.inject import Singleton
from micronaut.context.annotation import Requires

from .Book import Book


@Requires(property="spec.name", value="BookControllerTest")
@Singleton
class BookRepository:

    def __init__(self):
        self.books: list[Book] = []

    def find_all(self) -> list[Book]:
        return list(self.books)

    def save(self, book: Book) -> Book:
        self.books.append(book)
        return book

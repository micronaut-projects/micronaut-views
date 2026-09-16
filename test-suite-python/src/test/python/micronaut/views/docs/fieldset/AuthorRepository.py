from jakarta.inject import Singleton

from .Author import Author


@Singleton
class AuthorRepository:

    def find_all(self) -> list[Author]:
        return [
            Author(1, "Kishori Sharan"),
            Author(2, "Peter Späth"),
            Author(3, "Sam Newman"),
        ]

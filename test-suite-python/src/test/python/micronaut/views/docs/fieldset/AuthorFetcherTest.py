from typing import Annotated

from jakarta.inject import Inject
from micronaut.test.extensions.junit5.annotation import MicronautTest
from micronaut.views.fields import FormGenerator
from micronaut.views.fields.elements import InputHiddenFormElement, SelectFormElement
from org.junit.jupiter.api import Test

from .BookAuthorSave import BookAuthorSave


@MicronautTest(startApplication=False)
class AuthorFetcherTest:
    formGenerator: Annotated[FormGenerator, Inject]

    @Test
    def test_the_options_of_the_select_are_loaded_with_the_fetcher(self):
        form = self.formGenerator.generate("/books/authors/save", BookAuthorSave)
        assert form.action() == "/books/authors/save"
        fields = form.fieldset().fields()
        book_id = fields.get(0)
        assert isinstance(book_id, InputHiddenFormElement)
        assert book_id.name() == "bookId"
        author_id = fields.get(1)
        assert isinstance(author_id, SelectFormElement)
        assert author_id.name() == "authorId"
        options = list(author_id.options())
        assert [o.value() for o in options] == ["1", "2", "3"]
        assert [o.label().defaultMessage() for o in options] == ["Kishori Sharan", "Peter Späth", "Sam Newman"]
        assert not any(o.selected() for o in options)

        form = self.formGenerator.generate("/books/authors/save", BookAuthorSave(1, 3))
        author_id = form.fieldset().fields().get(1)
        assert [o.selected() for o in author_id.options()] == [False, False, True]

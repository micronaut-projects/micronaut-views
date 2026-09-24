from jakarta.inject import Singleton
from micronaut.views.fields.elements import Option
from micronaut.views.fields.fetchers import OptionFetcher
from micronaut.views.fields.messages import Message

from .AuthorRepository import AuthorRepository


# tag::clazz[]
@Singleton
class AuthorFetcher(OptionFetcher[int]):

    def __init__(self, author_repository: AuthorRepository):
        self.author_repository = author_repository

    def generate(self, type_or_instance) -> list[Option]:
        options = []
        for author in self.author_repository.find_all():
            builder = (Option.builder()
                       .value(str(author.id))
                       .label(Message.of(author.title)))
            if isinstance(type_or_instance, int):
                builder.selected(author.id == type_or_instance)
            options.append(builder.build())
        return options
# end::clazz[]

# TODO(python): @Select(fetcher=AuthorFetcher) needs the Python OptionFetcher implementation of AuthorFetcher.py, which
# cannot be generated (same-arity overloads generate(Class<T>) / generate(T)).
# The Java example is kept below, commented out, until Python compiler supports it (see DISABLED_TESTS.md).
# from dataclasses import dataclass
# from typing import Annotated
#
# from jakarta.validation.constraints import NotNull
# from micronaut.serde.annotation import Serdeable
# from micronaut.views.fields.annotations import InputHidden, Select
#
# from .AuthorFetcher import AuthorFetcher
#
#
# # tag::clazz[]
# @Serdeable
# @dataclass
# class BookAuthorSave:
#     bookId: Annotated[int, NotNull, InputHidden]
#     authorId: Annotated[int | None, NotNull, Select(fetcher=AuthorFetcher)]
# # end::clazz[]

from typing import Annotated

from jakarta.validation import ConstraintViolationException, Valid
from micronaut.context.annotation import Requires
from micronaut.http import HttpRequest, HttpResponse, MediaType
from micronaut.http.annotation import Body, Consumes, Controller, Error, Get, Post, Produces
from micronaut.http.uri import UriBuilder
from micronaut.views import ModelAndView, View
from micronaut.views.fields import FormGenerator

from .Book import Book
from .BookRepository import BookRepository
from .BookSave import BookSave


@Requires(property="spec.name", value="BookControllerTest")
# tag::clazz[]
@Controller("/books")
class BookController:
    CONTROLLER_PATH = "/books"
    SAVE_PATH = "/save"
    FORM = "form"

    def __init__(self, form_generator: FormGenerator, book_repository: BookRepository):
        self.form_generator = form_generator
        self.book_repository = book_repository

    @Produces(MediaType.TEXT_HTML)
    @View("/books/create.html")
    @Get("/create")
    def create(self) -> dict[str, object]:
        return {self.FORM: self.form_generator.generate(self.CONTROLLER_PATH + self.SAVE_PATH, BookSave)}

    @Produces(MediaType.TEXT_HTML)
    @View("/books/list.html")
    @Get("/list")
    def list(self) -> dict[str, object]:
        return {"books": self.book_repository.find_all()}

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post(SAVE_PATH)
    def save(self, bookSave: Annotated[BookSave, Valid, Body]) -> HttpResponse:
        self.book_repository.save(Book(bookSave.title, bookSave.pages))
        return HttpResponse.seeOther(UriBuilder.of(self.CONTROLLER_PATH).path("list").build())

    @Error(exception=ConstraintViolationException)
    def on_constraint_violation_exception(self, request: HttpRequest, ex: ConstraintViolationException) -> HttpResponse:
        if request.getPath() == self.CONTROLLER_PATH + self.SAVE_PATH:
            book_save = request.getBody(BookSave)
            if book_save.isPresent():
                form = self.form_generator.generate(self.CONTROLLER_PATH + self.SAVE_PATH, book_save.get(), ex)
                body = ModelAndView("/books/create.html", {self.FORM: form})
                return HttpResponse.unprocessableEntity().body(body)
        return HttpResponse.serverError()
# end::clazz[]

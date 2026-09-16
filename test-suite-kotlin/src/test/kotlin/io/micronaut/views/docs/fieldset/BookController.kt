package io.micronaut.views.docs.fieldset

import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Produces
import io.micronaut.http.uri.UriBuilder
import io.micronaut.views.ModelAndView
import io.micronaut.views.View
import io.micronaut.views.fields.FormGenerator
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Valid

@Requires(property = "spec.name", value = "BookControllerTest")
//tag::clazz[]
@Controller("/books")
open class BookController(private val formGenerator: FormGenerator,
                     private val bookRepository: BookRepository) {

    @Produces(MediaType.TEXT_HTML)
    @View("/books/create.html")
    @Get("/create")
    fun create(): Map<String, Any> {
        return mapOf(FORM to formGenerator.generate(CONTROLLER_PATH + SAVE_PATH, BookSave::class.java))
    }

    @Produces(MediaType.TEXT_HTML)
    @View("/books/list.html")
    @Get("/list")
    fun list(): Map<String, Any> {
        return mapOf("books" to bookRepository.findAll())
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post(SAVE_PATH)
    open fun save(@Valid @Body bookSave: BookSave): HttpResponse<*> {
        bookRepository.save(Book(bookSave.title, bookSave.pages))
        return HttpResponse.seeOther<Any>(UriBuilder.of(CONTROLLER_PATH).path("list").build())
    }

    @Error(exception = ConstraintViolationException::class)
    fun onConstraintViolationException(request: HttpRequest<*>, ex: ConstraintViolationException): HttpResponse<*> {
        if (request.path == CONTROLLER_PATH + SAVE_PATH) {
            val bookSave = request.getBody(BookSave::class.java).orElse(null)
            if (bookSave != null) {
                val form = formGenerator.generate(CONTROLLER_PATH + SAVE_PATH, bookSave, ex)
                val body = ModelAndView("/books/create.html", mapOf(FORM to form))
                return HttpResponse.unprocessableEntity<Any>().body(body)
            }
        }
        return HttpResponse.serverError<Any>()
    }

    companion object {
        const val CONTROLLER_PATH = "/books"
        const val SAVE_PATH = "/save"
        const val FORM = "form"
    }
}
//end::clazz[]

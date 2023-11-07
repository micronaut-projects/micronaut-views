package io.micronaut.views.fields.thymeleaf;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;
import io.micronaut.views.fields.Form;
import io.micronaut.views.fields.FormGenerator;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import io.micronaut.http.annotation.Error;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

//tag::clazz[]
@Controller("/books")
class BookController {
    public static final String CONTROLLER_PATH = "/books";
    public static final String SAVE_PATH = "/save";
    public static final String FORM = "form";

    private final FormGenerator formGenerator;
    private final BookRepository bookRepository;

    BookController(FormGenerator formGenerator,
                   BookRepository bookRepository) {
        this.formGenerator = formGenerator;
        this.bookRepository = bookRepository;
    }

    @Produces(MediaType.TEXT_HTML)
    @View("/books/create.html")
    @Get("/create")
    Map<String, Object> create() {
        return Collections.singletonMap(FORM,
                formGenerator.generate(CONTROLLER_PATH + SAVE_PATH, BookSave.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @View("/books/list.html")
    @Get("/list")
    Map<String, Object> list() {
        return Collections.singletonMap("books", bookRepository.findAll());
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post(SAVE_PATH)
    HttpResponse<?> save(@Valid @Body BookSave bookSave) {
        bookRepository.save(new Book(null, bookSave.title(), bookSave.pages()));
        return HttpResponse.seeOther(UriBuilder.of("/books").path("list").build());
    }

    @Error(exception = ConstraintViolationException.class)
    public HttpResponse<?> onConstraintViolationException(HttpRequest<?> request, ConstraintViolationException ex) {
        if (request.getPath().equals(CONTROLLER_PATH + SAVE_PATH)) {
            Optional<BookSave> bookSaveOptional = request.getBody(BookSave.class);
            if (bookSaveOptional.isPresent()) {
                Form form = formGenerator.generate(CONTROLLER_PATH + SAVE_PATH, bookSaveOptional.get(), ex);
                ModelAndView<Map<String, Object>> body = new ModelAndView<>("/books/create.html",
                        Collections.singletonMap(FORM, form));
                return HttpResponse.unprocessableEntity().body(body);
            }
        }
        return HttpResponse.serverError();
    }
}
//end::clazz[]

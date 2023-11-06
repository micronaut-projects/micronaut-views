package io.micronaut.views.fields.thymeleaf;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.views.View;
import io.micronaut.views.fields.FormGenerator;
import jakarta.validation.Valid;

import java.util.Collections;
import java.util.Map;

@Requires(property = "spec.name", value = "BookControllerTest")
//tag::clazz[]
@Controller("/books")
class BookController {

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
        return Collections.singletonMap("form",
                formGenerator.generate("/books/save", BookSave.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @View("/books/list.html")
    @Get("/list")
    Map<String, Object> list() {
        return Collections.singletonMap("books", bookRepository.findAll());
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/save")
    HttpResponse<?> save(@Valid @Body BookSave bookSave) {
        bookRepository.save(new Book(null, bookSave.title(), bookSave.pages()));
        return HttpResponse.seeOther(UriBuilder.of("/books").path("list").build());
    }
}
//end::clazz[]

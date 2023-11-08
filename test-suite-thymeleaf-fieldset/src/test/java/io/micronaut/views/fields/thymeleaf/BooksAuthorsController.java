package io.micronaut.views.fields.thymeleaf;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.views.View;
import io.micronaut.views.fields.FormGenerator;
import jakarta.validation.Valid;

import java.util.Collections;
import java.util.Map;

@Controller("/books")
class BooksAuthorsController {
    private final FormGenerator formGenerator;
    private final BookAuthorRepository bookAuthorRepository;

    BooksAuthorsController(FormGenerator formGenerator,
                           BookAuthorRepository bookAuthorRepository) {
        this.formGenerator = formGenerator;
        this.bookAuthorRepository = bookAuthorRepository;
    }

    @Get("/{bookId}/authors/create")
    @Produces(MediaType.TEXT_HTML)
    @View("/books/authorCreate.html")
    Map<String, Object> bookAuthorCreate(@PathVariable Long bookId) {
        return Collections.singletonMap("form", formGenerator.generate("/books/authors/save", new BookAuthorSave(bookId, null)));
    }

    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    @Post("/{bookId}/authors/save")
    HttpResponse<?> save(@PathVariable Long bookId, @Body @Valid BookAuthorSave bookAuthorSave) {
        bookAuthorRepository.save(new BookAuthor(new BookAuthorId(bookAuthorSave.bookId(), bookAuthorSave.authorId())));
        return HttpResponse.seeOther(UriBuilder.of("/books").path("list").build());
    }
}

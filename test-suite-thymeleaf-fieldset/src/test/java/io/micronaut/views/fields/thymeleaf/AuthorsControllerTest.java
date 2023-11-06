package io.micronaut.views.fields.thymeleaf;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static io.micronaut.views.fields.thymeleaf.TestUtils.formPost;
import static io.micronaut.views.fields.thymeleaf.TestUtils.htmlGet;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "datasources.default.password", value = "")
@Property(name = "datasources.default.dialect", value = "H2")
@Property(name = "datasources.default.schema-generate", value = "CREATE_DROP")
@Property(name = "datasources.default.url", value = "jdbc:h2:mem:authorTestDb;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE")
@Property(name = "datasources.default.username", value = "sa")
@Property(name = "datasources.default.driver-class-name=org.h2.Driver")
@MicronautTest(transactional = false)
class AuthorsControllerTest {

    @Test
    void renderADropDown(@Client("/") HttpClient httpClient,
                         AuthorRepository authorRepository,
                         BookRepository bookRepository,
                         BookAuthorRepository bookAuthorRepository) {
        Author kishori = authorRepository.save(new Author(null, "Kishori Sharan"));
        Author peter = authorRepository.save(new Author(null, "Peter Späth"));
        Author sam = authorRepository.save(new Author(null, "Sam Newman"));

        Book book = bookRepository.save(new Book(null, "Building Microservices", 120));

        BlockingHttpClient client = httpClient.toBlocking();
        String createPath = UriBuilder.of("/books").path(String.valueOf(book.id())).path("authors").path("create").build().toString();
        String html = assertDoesNotThrow(() -> client.retrieve(htmlGet(createPath)));
        assertTrue(html.contains("<!DOCTYPE html>"));
        assertTrue(html.contains("<form action=\"/books/authors/save\" method=\"post\"><input type=\"hidden\" name=\"bookId\" value=\"" + book.id() +"\"/><div class=\"mb-3\"><label for=\"authorId\" class=\"form-label\">Author Id</label><select name=\"authorId\" id=\"authorId\" class=\"form-select\" required=\"required\"><option value=\""+ kishori.id() +"\">Kishori Sharan</option><option value=\""+ peter.id() +"\">Peter Späth</option><option value=\""+ sam.id() +"\">Sam Newman</option></select></div><input type=\"submit\" value=\"Submit\" class=\"btn btn-primary\"/></form>"));
        long count = bookAuthorRepository.count();
        String savePath = UriBuilder.of("/books").path(String.valueOf(book.id())).path("authors").path("save").build().toString();
        assertDoesNotThrow(() -> client.retrieve(formPost(savePath, "bookId=" + book.id() + "&authorId=" + sam.id())));
        assertEquals(count + 1, bookAuthorRepository.count());

        bookAuthorRepository.deleteAll();
        authorRepository.delete(sam);
        authorRepository.delete(peter);
        authorRepository.delete(kishori);

        bookRepository.delete(book);
    }

}

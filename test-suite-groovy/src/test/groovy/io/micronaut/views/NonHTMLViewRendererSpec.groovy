package io.micronaut.views

import groovy.json.JsonSlurper
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.annotation.Nullable
import io.micronaut.core.io.Writable
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.serde.annotation.Serdeable
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

import javax.inject.Singleton
import java.util.stream.Collectors

@Property(name = "spec.name", value = "CsvViewRendererSpec")
@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@MicronautTest
class NonHTMLViewRendererSpec extends Specification {

    @Inject
    @Client("/")
    HttpClient httpClient

    void 'test a default HTML views renderer eg Velocity is picked if not typed rendered for MediaType is found'() {
        given:
        BlockingHttpClient client = httpClient.toBlocking()

        when:
        String html = client.retrieve(HttpRequest.GET("/books").accept(MediaType.TEXT_HTML), String.class)

        then:
        html.contains("<h2>#books: 3</h2>")
    }

    void 'if no view resolved it renders as json'() {
        given:
        BlockingHttpClient client = httpClient.toBlocking()

        when:
        String json = client.retrieve(HttpRequest.GET("/books").accept(MediaType.APPLICATION_JSON), String.class)
        def parsed = new JsonSlurper().parseText(json)

        then:
        parsed == [books: [
                [isbn: "1491950358", name: "Building Microservices"],
                [isbn: "1680502395", name: "Release It!"],
                [isbn: "0321601912", name: "Continuous Delivery"]]
        ]
    }

    void 'you can register views renderer for media types other than HTML'() {
        given:
        BlockingHttpClient client = httpClient.toBlocking()

        when:
        String csv = client.retrieve(HttpRequest.GET("/books").accept(MediaType.TEXT_CSV), String.class)

        then:
        csv == '''1491950358,Building Microservices
                 |1680502395,Release It!
                 |0321601912,Continuous Delivery'''.stripMargin()

        when:
        String xml = client.retrieve(HttpRequest.GET("/books").accept(MediaType.TEXT_XML), String.class)

        then:
        xml == '''<books>
                 |<book><isbn>1491950358</isbn><name>Building Microservices</name></book>
                 |<book><isbn>1680502395</isbn><name>Release It!</name></book>
                 |<book><isbn>0321601912</isbn><name>Continuous Delivery</name></book>
                 |</books>'''.stripMargin()
    }

    @Produces(MediaType.TEXT_XML)
    @Requires(property = "spec.name", value = "CsvViewRendererSpec")
    @Singleton
    static class XmlViewRenderer implements ViewsRenderer<Library, HttpRequest<?>> {
        @Override
        @NonNull
        Writable render(@NonNull String viewName, @Nullable Library data, @Nullable HttpRequest<?> request) {
            return new Writable() {
                @Override
                void writeTo(Writer out) throws IOException {
                    out.write("<books>\n" + String.join("\n", data.getBooks().stream()
                            .map(b -> "<book><isbn>" + b.getIsbn() + "</isbn><name>" + b.getName() + "</name></book>")
                            .collect(Collectors.toList())) + "\n</books>")
                }
            }
        }

        @Override
        boolean exists(@NonNull String viewName) {
            return "books".equalsIgnoreCase(viewName)
        }
    }

    @Produces(MediaType.TEXT_CSV)
    @Requires(property = "spec.name", value = "CsvViewRendererSpec")
    @Singleton
    static class SingleBookViewRenderer implements ViewsRenderer<Book, HttpRequest<?>> {
        // this renderer should not be used because it specifies a different type

        @Override
        @NonNull
        Writable render(@NonNull String viewName,
                        @Nullable Book data,
                        @Nullable HttpRequest<?> request) {
            return new Writable() {
                @Override
                void writeTo(Writer out) throws IOException {
                    out.write("FAIL")
                }
            }
        }

        @Override
        boolean exists(@NonNull String viewName) {
            return true
        }

        @Override
        int getOrder() {
            return HIGHEST_PRECEDENCE
        }
    }

    @Produces(MediaType.TEXT_CSV)
    @Requires(property = "spec.name", value = "CsvViewRendererSpec")
    @Singleton
    static class CsvViewRenderer implements ViewsRenderer<Library, HttpRequest<?>> {
        @Override
        @NonNull
        Writable render(@NonNull String viewName,
                        @Nullable Library data,
                        @Nullable HttpRequest<?> request) {
            return new Writable() {
                @Override
                void writeTo(Writer out) throws IOException {
                    out.write(String.join("\n", data.getBooks().stream()
                            .map(b -> b.getIsbn() + "," + b.getName())
                            .collect(Collectors.toList())))
                }
            }
        }

        @Override
        boolean exists(@NonNull String viewName) {
            return "books".equalsIgnoreCase(viewName)
        }
    }

    @Serdeable
    static class Book {
        String isbn
        String name

        Book(String isbn, String name) {
            this.isbn = isbn
            this.name = name
        }

        String getIsbn() {
            return isbn
        }

        String getName() {
            return name
        }
    }

    @Serdeable
    static class Library {
        List<Book> books

        Library(List<Book> books) {
            this.books = books
        }

        List<Book> getBooks() {
            return books
        }
    }

    @Requires(property = "spec.name", value = "CsvViewRendererSpec")
    @Controller("/books")
    static class BooksController {
        static final Library books = new Library(Arrays.asList(new Book("1491950358", "Building Microservices"),
                new Book("1680502395", "Release It!"),
                new Book("0321601912", "Continuous Delivery")))

        @View("books")
        @Get(produces = MediaType.TEXT_CSV)
        Library index() {
            return books
        }

        @Produces(MediaType.TEXT_HTML)
        @View("books")
        @Get
        Library booksHtml() {
            return books
        }

        @Get
        Library json() {
            return books
        }

        @View("books")
        @Get(produces = MediaType.TEXT_XML)
        Library booksXml() {
            return books
        }
    }
}

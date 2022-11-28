package views;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.io.Writable;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.View;
import io.micronaut.views.ViewsRenderer;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "spec.name", value = "CsvViewRendererSpec")
@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@MicronautTest
class NonHTMLViewRendererTest {

    @Inject
    @Client("/")
    HttpClient httpClient;

    @Test
    void testADefaultHTMLViewsRendererEGVelocityIsPickedIfNotTypedRenderedForMediaTypeIsFound() {
        //given:
        BlockingHttpClient client = httpClient.toBlocking();
        //when:
        String html = client.retrieve(HttpRequest.GET("/books").accept(MediaType.TEXT_HTML), String.class);

        //then:
        assertTrue(html.contains("<h2>#books: 3</h2>"));
    }

    @Test
    void ifNoViewResolvedItRendersAsJson() {
        //given:
        BlockingHttpClient client = httpClient.toBlocking();
        //when:
        String json = client.retrieve(HttpRequest.GET("/books").accept(MediaType.APPLICATION_JSON), String.class);

        //then:
        assertTrue(json.startsWith("{\"books\":"));
        assertTrue(json.contains("{\"isbn\":\"1491950358\",\"name\":\"Building Microservices\"}"));
        assertTrue(json.contains("{\"isbn\":\"1680502395\",\"name\":\"Release It!\"}"));
        assertTrue(json.contains("{\"isbn\":\"0321601912\",\"name\":\"Continuous Delivery\"}"));
    }

    @Test
    void youCanRegisterViewsRendererForMediaTypesOtherThanHTML() {
        //given:
        BlockingHttpClient client = httpClient.toBlocking();
        //when:
        String csv = client.retrieve(HttpRequest.GET("/books").accept(MediaType.TEXT_CSV), String.class);

        //then:
        assertEquals("1491950358,Building Microservices\n"+
"1680502395,Release It!\n"+
"0321601912,Continuous Delivery", csv);

        //when:
        String xml = client.retrieve(HttpRequest.GET("/books").accept(MediaType.TEXT_XML), String.class);

        //then:
        assertEquals("<books>\n"+
"<book><isbn>1491950358</isbn><name>Building Microservices</name></book>\n"+
"<book><isbn>1680502395</isbn><name>Release It!</name></book>\n"+
"<book><isbn>0321601912</isbn><name>Continuous Delivery</name></book>\n"+
"</books>", xml);
    }

    @Produces(MediaType.TEXT_XML)
    @Requires(property = "spec.name", value = "CsvViewRendererSpec")
    @Singleton
    static class XmlViewRenderer implements ViewsRenderer<Library> {
        @Override
        @NonNull
        public Writable render(@NonNull String viewName, @Nullable Library data, @Nullable HttpRequest<?> request) {
            return new Writable() {
                @Override
                public void writeTo(Writer out) throws IOException {
                    out.write("<books>\n" + String.join("\n", data.getBooks().stream()
                            .map(b -> "<book><isbn>" + b.getIsbn() + "</isbn><name>" + b.getName() +  "</name></book>" )
                            .collect(Collectors.toList())) + "\n</books>");
                }
            };
        }

        @Override
        public boolean exists(@NonNull String viewName) {
            return "books".equalsIgnoreCase(viewName);
        }
    }

    @Produces(MediaType.TEXT_CSV)
    @Requires(property = "spec.name", value = "CsvViewRendererSpec")
    @Singleton
    public static class SingleBookViewRenderer implements ViewsRenderer<Book> {
        // this renderer should not be used because it specifies a different type

        @Override
        @NonNull
        public Writable render(@NonNull String viewName,
                               @Nullable Book data,
                               @Nullable HttpRequest<?> request) {
            return new Writable() {
                @Override
                public void writeTo(Writer out) throws IOException {
                    out.write("FAIL");
                }
            };
        }

        @Override
        public boolean exists(@NonNull String viewName) {
            return true;
        }

        @Override
        public int getOrder() {
            return HIGHEST_PRECEDENCE;
        }
    }

    @Produces(MediaType.TEXT_CSV)
    @Requires(property = "spec.name", value = "CsvViewRendererSpec")
    @Singleton
    public static class CsvViewRenderer implements ViewsRenderer<Library> {
        @Override
        @NonNull
        public Writable render(@NonNull String viewName,
                               @Nullable Library data,
                               @Nullable HttpRequest<?> request) {
            return new Writable() {
                @Override
                public void writeTo(Writer out) throws IOException {
                    out.write(String.join("\n", data.getBooks().stream()
                            .map(b -> b.getIsbn() + "," + b.getName())
                            .collect(Collectors.toList())));
                }
            };
        }

        @Override
        public boolean exists(@NonNull String viewName) {
            return "books".equalsIgnoreCase(viewName);
        }
    }

    @Serdeable
    public static class Book {
        String isbn;
        String name;
        public Book(String isbn, String name) {
            this.isbn = isbn;
            this.name = name;
        }
        public String getIsbn() {
            return isbn;
        }
        public String getName() {
            return name;
        }
    }

    @Serdeable
    public static class Library {
        List<Book> books;
        public Library(List<Book> books) {
            this.books = books;
        }

        public List<Book> getBooks() {
            return books;
        }
    }

    @Requires(property = "spec.name", value = "CsvViewRendererSpec")
    @Controller("/books")
    public static class BooksController {
        static final Library books = new Library(Arrays.asList(new Book("1491950358", "Building Microservices"),
         new Book("1680502395", "Release It!"),
         new Book("0321601912", "Continuous Delivery")));

        @View("books")
        @Get(produces = MediaType.TEXT_CSV)
        Library index() {
            return books;
        }

        @Produces(MediaType.TEXT_HTML)
        @View("books")
        @Get
        Library booksHtml() {
            return books;
        }

        @Get
        Library json() {
            return books;
        }

        @View("books")
        @Get(produces = MediaType.TEXT_XML)
        Library booksXml() {
            return books;
        }
    }
}

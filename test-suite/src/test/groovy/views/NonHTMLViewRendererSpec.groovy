package views

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.annotation.Nullable
import io.micronaut.core.io.Writable
import io.micronaut.core.order.Ordered
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.views.View
import io.micronaut.views.ViewsRenderer
import io.micronaut.views.WritableViewsRenderer
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification
import javax.inject.Singleton
import java.util.stream.Collectors

class NonHTMLViewRendererSpec extends Specification {

    @AutoCleanup
    @Shared
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, [
            'micronaut.security.enabled': false,
            'spec.name': 'CsvViewRendererSpec',
            'micronaut.views.soy.enabled': false,
    ])

    @AutoCleanup
    @Shared
    HttpClient httpClient = embeddedServer.applicationContext.createBean(HttpClient, embeddedServer.URL)

    @Shared
    BlockingHttpClient client = httpClient.toBlocking()

    void "A default HTML ViewsRenderer - e.g. Velocity - is picked if not typed rendered for media type is found"() {
        when:
        String html = client.retrieve(HttpRequest.GET('/books').accept(MediaType.TEXT_HTML), String)

        then:
        noExceptionThrown()
        html.contains('<h2>#books: 3</h2>')
    }

    void "If no view resolved, it renders as json"() {
        when:
        String json = client.retrieve(HttpRequest.GET('/books').accept(MediaType.APPLICATION_JSON), String)

        then:
        noExceptionThrown()
        json.startsWith('{"books":')
        json.contains('{"isbn":"1491950358","name":"Building Microservices"}')
        json.contains('{"isbn":"1680502395","name":"Release It!"}')
        json.contains('{"isbn":"0321601912","name":"Continuous Delivery"}')
    }

    void "You can register ViewsRenderer for media types other than HTML"() {
        when:
        String csv = client.retrieve(HttpRequest.GET('/books').accept(MediaType.TEXT_CSV), String)

        then:
        noExceptionThrown()
        csv == '''\
1491950358,Building Microservices
1680502395,Release It!
0321601912,Continuous Delivery'''

        when:
        String xml = client.retrieve(HttpRequest.GET('/books').accept(MediaType.TEXT_XML), String)

        then:
        noExceptionThrown()
        xml == '''\
<books>
<book><isbn>1491950358</isbn><name>Building Microservices</name></book>
<book><isbn>1680502395</isbn><name>Release It!</name></book>
<book><isbn>0321601912</isbn><name>Continuous Delivery</name></book>
</books>'''
    }

    @Produces(MediaType.TEXT_XML)
    @Requires(property = "spec.name", value = "CsvViewRendererSpec")
    @Singleton
    static class XmlViewRenderer implements WritableViewsRenderer<Library> {
        @Override
        Writable render(@NonNull String viewName, @Nullable Library data, @NonNull HttpRequest<?> request) {
            new Writable() {
                @Override
                void writeTo(Writer out) throws IOException {
                    out.write("<books>\n" + String.join("\n", data.getBooks().stream()
                            .map(b -> "<book><isbn>" + b.getIsbn() + "</isbn><name>" + b.getName() +  "</name></book>" )
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
    static class SingleBookViewRenderer implements WritableViewsRenderer<Book> {
        // this renderer should not be used because it specifies a different type

        @Override
        Writable render(@NonNull String viewName, @Nullable Book data, @NonNull HttpRequest<?> request) {
            new Writable() {
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
            HIGHEST_PRECEDENCE
        }
    }

    @Produces(MediaType.TEXT_CSV)
    @Requires(property = "spec.name", value = "CsvViewRendererSpec")
    @Singleton
    static class CsvViewRenderer implements WritableViewsRenderer<Library> {
        @Override
        Writable render(@NonNull String viewName, @Nullable Library data, @NonNull HttpRequest<?> request) {
            new Writable() {
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

    @Introspected
    static class Book {
        String isbn
        String name
        Book(String isbn, String name) {
            this.isbn = isbn
            this.name = name
        }
    }


    @Introspected
    static class Library {
        List<Book> books
        Library(List<Book> books) {
            this.books = books
        }
    }

    @Requires(property = "spec.name", value = "CsvViewRendererSpec")
    @Controller("/books")
    static class BooksController {
        static final Library books = new Library([new Book("1491950358", "Building Microservices"),
         new Book("1680502395", "Release It!"),
         new Book("0321601912", "Continuous Delivery")])

        @View("books")
        @Get(produces = MediaType.TEXT_CSV)
        Library index() {
            books
        }

        @Produces(MediaType.TEXT_HTML)
        @View("books")
        @Get
        Library booksHtml() {
            books
        }

        @Get
        Library json() {
            books
        }

        @View("books")
        @Get(produces = MediaType.TEXT_XML)
        Library booksXml() {
            books
        }
    }
}

package io.micronaut.views

import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.annotation.Nullable
import io.micronaut.core.io.Writable
import io.micronaut.core.order.Ordered
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.serde.annotation.Serdeable
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import javax.inject.Singleton
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@Property(name = "spec.name", value = "CsvViewRendererSpec")
@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@MicronautTest
class NonHTMLViewRendererTest() {

    @Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @Test
    fun testADefaultHTMLViewsRendererEGVelocityIsPickedIfNotTypedRenderedForMediaTypeIsFound() {
        //given:
        val client = httpClient.toBlocking()

        //when:
        val html = client.retrieve(
            HttpRequest.GET<String>("/books").accept(MediaType.TEXT_HTML)
        )

        //then:
        Assertions.assertTrue(html.contains("<h2>#books: 3</h2>"))
    }

    @Test
    fun ifNoViewResolvedItRendersAsJson() {
        //given:
        val client = httpClient.toBlocking()

        //when:
        val json = client.retrieve(
            HttpRequest.GET<String>("/books").accept(MediaType.APPLICATION_JSON)
        )

        //then:
        Assertions.assertTrue(json.startsWith("""{"books":"""))
        Assertions.assertTrue(json.contains("""{"isbn":"1491950358","name":"Building Microservices"}"""))
        Assertions.assertTrue(json.contains("""{"isbn":"1680502395","name":"Release It!"}"""))
        Assertions.assertTrue(json.contains("""{"isbn":"0321601912","name":"Continuous Delivery"}"""))
    }

    @Test
    fun youCanRegisterViewsRendererForMediaTypesOtherThanHTML() {
        //given:
        val client = httpClient.toBlocking()
        //when:
        val csv = client.retrieve(
            HttpRequest.GET<String>("/books").accept(MediaType.TEXT_CSV)
        )

        //then:
        Assertions.assertEquals(
            """
            1491950358,Building Microservices
            1680502395,Release It!
            0321601912,Continuous Delivery""".trimIndent(),
            csv
        )

        //when:
        val xml = client.retrieve(
            HttpRequest.GET<String>("/books").accept(MediaType.TEXT_XML)
        )

        //then:
        Assertions.assertEquals(
            """
            <books>
            <book><isbn>1491950358</isbn><name>Building Microservices</name></book>
            <book><isbn>1680502395</isbn><name>Release It!</name></book>
            <book><isbn>0321601912</isbn><name>Continuous Delivery</name></book>
            </books>""".trimIndent(),
            xml
        )
    }

    @Produces(MediaType.TEXT_XML)
    @Requires(property = "spec.name", value = "CsvViewRendererSpec")
    @Singleton
    class XmlViewRenderer() : ViewsRenderer<Library, HttpRequest<*>> {

        override fun render(
            viewName: String,
            data: Library,
            request: HttpRequest<*>
        ) = Writable { out ->
            out.write("<books>\n")
            out.write(
                data.books.joinToString("\n") { "<book><isbn>${it.isbn}</isbn><name>${it.name}</name></book>" }
            )
            out.write("\n</books>")
        }

        override fun exists(viewName: @NonNull String?): Boolean {
            return "books".equals(viewName, ignoreCase = true)
        }
    }

    @Produces(MediaType.TEXT_CSV)
    @Requires(property = "spec.name", value = "CsvViewRendererSpec")
    @Singleton
    class SingleBookViewRenderer() : ViewsRenderer<Book, HttpRequest<*>> {

        // this renderer should not be used because it specifies a different type
        override fun render(viewName: @NonNull String, data: @Nullable Book, request: @Nullable HttpRequest<*>) =
            Writable { out -> out.write("FAIL") }

        override fun exists(viewName: @NonNull String?): Boolean {
            return true
        }

        override fun getOrder(): Int {
            return Ordered.HIGHEST_PRECEDENCE
        }
    }

    @Produces(MediaType.TEXT_CSV)
    @Requires(property = "spec.name", value = "CsvViewRendererSpec")
    @Singleton
    class CsvViewRenderer() : ViewsRenderer<Library, HttpRequest<*>> {

        override fun render(viewName: String?, data: Library, request: HttpRequest<*>) = Writable { out ->
            out.write(data.books.joinToString("\n") { "${it.isbn},${it.name}" })
        }

        override fun exists(viewName: String) = "books".equals(viewName, ignoreCase = true)
    }

    @Serdeable
    data class Book(val isbn: String, val name: String)

    @Serdeable
    data class Library(val books: List<Book>)

    @Requires(property = "spec.name", value = "CsvViewRendererSpec")
    @Controller("/books")
    class BooksController() {

        val books: Library = Library(
            listOf(
                Book("1491950358", "Building Microservices"),
                Book("1680502395", "Release It!"),
                Book("0321601912", "Continuous Delivery")
            )
        )

        @View("books")
        @Get(produces = [MediaType.TEXT_CSV])
        fun index() = books

        @Produces(MediaType.TEXT_HTML)
        @View("books")
        @Get
        fun booksHtml() = books

        @Get
        fun json() = books

        @View("books")
        @Get(produces = [MediaType.TEXT_XML])
        fun booksXml() = books
    }
}

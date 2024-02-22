package io.micronaut.views

import com.github.jknack.handlebars.Handlebars
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Replaces
import io.micronaut.context.annotation.Requires
import io.micronaut.core.io.scan.ClassPathResourceLoader
import io.micronaut.core.order.Ordered
import io.micronaut.core.util.StringUtils
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.views.handlebars.HandlebarsViewsRenderer
import io.micronaut.views.handlebars.HandlebarsViewsRendererConfiguration
import io.micronaut.views.handlebars.HandlebarsViewsRendererConfigurationProperties
import jakarta.inject.Inject
import javax.inject.Singleton
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@Property(name = "spec.name", value = "MultipleViewRendererSpec")
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@MicronautTest
internal class MultipleViewRendererTest {

    @Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @Test
    fun multipleViewsRendererArePossible() {
        //given:
        val client = httpClient.toBlocking()

        //when:
        var html = client.retrieve("/stark", String::class.java)

        //then:
        assertTrue(html.contains("John Snow"))

        //when:
        html = client.retrieve("/targaryen", String::class.java)

        //then:
        assertTrue(html.contains("Aegon Targaryen"))
    }

    @Test
    fun viewsWithSameNameAreSelectedWithViewsRendererOrder() {
        //given:
        val client = httpClient.toBlocking()

        //when:
        val html = client.retrieve("/johnsnow", String::class.java)

        //then:
        assertTrue(html.contains("John Snow"))
    }

    @Requires(property = "spec.name", value = "MultipleViewRendererSpec")
    @Produces(MediaType.TEXT_HTML)
    @Requires(property = HandlebarsViewsRendererConfigurationProperties.PREFIX + ".enabled", notEquals = StringUtils.FALSE)
    @Requires(classes = [Handlebars::class])
    @Replaces(HandlebarsViewsRenderer::class)
    @Singleton
    internal class CustomHandlebarsViewsRenderer(
        viewsConfiguration: ViewsConfiguration,
        resourceLoader: ClassPathResourceLoader,
        handlebarsViewsRendererConfiguration: HandlebarsViewsRendererConfiguration,
        handlebars: Handlebars
    ) : HandlebarsViewsRenderer<Any, Any>(viewsConfiguration, resourceLoader, handlebarsViewsRendererConfiguration, handlebars) {

        override fun getOrder() = Ordered.HIGHEST_PRECEDENCE + 100
    }

    @Controller
    @Requires(property = "spec.name", value = "MultipleViewRendererSpec")
    class GotController {

        @View("stark")
        @Get("/stark")
        fun stark() = mapOf<String, Any>()

        @View("johnsnow")
        @Get("/johnsnow")
        fun johnsnow() = mapOf<String, Any>()

        @View("targaryen")
        @Get("/targaryen")
        fun targaryen() = mapOf<String, Any>()
    }
}

package io.micronaut.views

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.Introspected
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.ApplicationConfiguration
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.views.model.ConfigViewModelProcessor
import io.micronaut.views.model.FruitsController
import io.micronaut.views.model.ViewModelProcessor
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

@Property(name = "spec.name", value = "ModelAndViewSpec")
@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.security.views-model-decorator.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.application.name", value = "test")
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@MicronautTest
class ModelAndViewTest {

    @Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @Inject
    lateinit var beanContext: BeanContext

    @Test
    fun aViewModelCanBeAnyObject() {
        //given:
        val client = httpClient.toBlocking()
        ////expect:
        assertTrue(beanContext.containsBean(FruitsController::class.java))

        ////when:
        val request: HttpRequest<*> = HttpRequest.GET<Any>("/")
        val response = client.exchange(request, String::class.java)

        //then:
        assertEquals(HttpStatus.OK, response.status())

        //when:
        val html = response.body()

        //then:
        assertNotNull(html)

        //and:
        assertTrue(html.contains("<h1>fruit: apple</h1>"))

        //and:
        assertTrue(html.contains("<h1>color: red</h1>"))
    }

    @Test
    fun returningANullModelCausesA404() {
        //given:
        val client = httpClient.toBlocking()
        //expect:
        assertTrue(beanContext.containsBean(FruitsController::class.java))

        //when:
        val e = Executable {
            client.exchange(
                HttpRequest.GET<Any>("/null"),
                String::class.java
            )
        }

        //then:
        assertThrows(
            HttpClientResponseException::class.java, e
        )
    }

    @Test
    fun aViewModelCanBeAMap() {
        //given:
        val client = httpClient.toBlocking()
        //expect:
        assertTrue(beanContext.containsBean(FruitsController::class.java))

        //when:
        val request = HttpRequest.GET<Any>("/map")
        val response = client.exchange(request, String::class.java)

        //then:
        assertEquals(HttpStatus.OK, response.status())

        //when:
        val html = response.body()

        //then:
        assertNotNull(html)

        //and:
        assertTrue(html.contains("<h1>fruit: orange</h1>"))

        //and:
        assertTrue(html.contains("<h1>color: orange</h1>"))
    }

    @Test
    fun modelsCanBeDynamicallyEnhanced() {
        //given:
        val client = httpClient.toBlocking()

        //expect:
        assertTrue(beanContext.containsBean(FruitsController::class.java))

        //when:
        val request = HttpRequest.GET<Any>("/processor")
        val response = client.exchange(request, String::class.java)

        //then:
        assertEquals(HttpStatus.OK, response.status())

        //when:
        val html = response.body()

        //then:
        assertNotNull(html)

        //and:
        assertTrue(beanContext.containsBean(ConfigViewModelProcessor::class.java))

        //and:
        assertTrue(html.contains("<h1>config: test</h1>"))
    }

    @Test
    fun viewModelProcessorsWorkWithControllersReturningPOJOs() {
        //given:
        val client = httpClient.toBlocking()

        //expect:
        assertTrue(beanContext.containsBean(FruitsController::class.java))

        //when:
        val request: HttpRequest<*> = HttpRequest.GET<Any>("/pojo-processor")
        val response = client.exchange(request, String::class.java)

        //then:
        assertEquals(HttpStatus.OK, response.status())

        //when:
        val html = response.body()

        //then:
        assertNotNull(html)

        //and:
        assertTrue(beanContext.containsBean(ConfigViewModelProcessor::class.java))

        //and:
        assertTrue(html.contains("<h1>config: test</h1>"))
    }

    @Controller
    @Requires(property = "spec.name", value = "ModelAndViewSpec")
    class ViewModelProcessorController {

        @View("fruits-processor")
        @Get("/pojo-processor")
        fun pojoProcessor() = Fruit("orange", "orange")
    }

    @Introspected
    abstract class AbstractView(var applicationName: String? = null)

    @Introspected
    class Fruit(var name: String, var color: String) : AbstractView()

    @Singleton
    @Requires(property = "spec.name", value = "ModelAndViewSpec")
    class CustomViewModelProcessor(private val config: ApplicationConfiguration) :
        ViewModelProcessor<AbstractView> {

        override fun process(request: HttpRequest<*>, modelAndView: ModelAndView<AbstractView>) {
            modelAndView.model.ifPresent { model: AbstractView ->
                config.name.ifPresent { name -> model.applicationName = name }
            }
        }
    }
}

package io.micronaut.views.docs.views

import io.micronaut.context.annotation.Property
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@Property(name = "spec.name", value = "ViewsControllerTest")
@MicronautTest
class ViewsControllerSpec extends Specification {

    @Inject
    @Client("/")
    HttpClient httpClient

    void "a view can be rendered from a map, a POJO or a ModelAndView"() {
        given:
        def client = httpClient.toBlocking()

        expect:
        ["/views", "/views/pojo", "/views/modelAndView"].every { path ->
            client.retrieve(HttpRequest.GET(path), String).contains("<h1>username: <span>sdelamo</span></h1>")
        }
    }
}

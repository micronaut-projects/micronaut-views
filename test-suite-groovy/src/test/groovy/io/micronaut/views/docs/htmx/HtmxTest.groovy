package io.micronaut.views.docs.htmx

import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.annotation.Nullable
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.views.ModelAndView
import io.micronaut.views.docs.turbo.Fruit
import io.micronaut.views.htmx.http.HtmxRequestHeaders
import io.micronaut.views.htmx.http.HtmxResponse
import io.micronaut.views.htmx.http.HtmxResponseHeaders
import jakarta.inject.Inject
import spock.lang.Specification

@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@Property(name = "spec.name", value = "HtmxRequestHeadersTest")
@MicronautTest
class HtmxTest extends Specification {

    @Inject
    @Client("/")
    HttpClient httpClient

    void testHtmxRequestHeaders() {
        given:
        BlockingHttpClient client = httpClient.toBlocking()

        when:
        String html = client.retrieve(HttpRequest.GET("/fruits/htmx").header("HX-Request", StringUtils.TRUE))
        then:
        '<h1>fruit: Apple</h1>\n<h2>color: Red</h2>\n' == html
        when:
        html = client.retrieve(HttpRequest.GET("/fruits/htmx"))
        then:
        html.contains("<!DOCTYPE html>")
    }

    void testOutOfBandSwaps() {
        given:
        BlockingHttpClient client = httpClient.toBlocking()
        when:
        String html = client.retrieve(HttpRequest.POST("/fruits/htmx", Collections.emptyMap()).header("HX-Request", StringUtils.TRUE));
        then:
        '<h1>fruit: Apple</h1>\n<h2>color: Red</h2>\n<div id="message" hx-swap-oob="true">Swap me directly!</div>' == html
    }

    void htmxResponseHeaders() {
        given:
        BlockingHttpClient client = httpClient.toBlocking();

        when:
        HttpResponse<?> response = client.exchange(HttpRequest.GET("/fruits/htmx/responseHeaders").header("HX-Request", StringUtils.TRUE));

        then:
        StringUtils.TRUE == response.getHeaders().get("HX-Refresh")
    }

    @Requires(property = "spec.name", value = "HtmxRequestHeadersTest")
    @Controller("/fruits/htmx")
    static class HtmxRequestHeadersController {
//tag::htmxRequestHeaders[]
        @Get
        ModelAndView<Map<String, Object>> index(@Nullable HtmxRequestHeaders htmxRequestHeaders) {
            Map<String, Object> model = [fruit: new Fruit("Apple", "Red")]
            if (htmxRequestHeaders != null) {
                return new ModelAndView<>("fruit", model)
            }
            new ModelAndView<>("fruits", model)
        }
//end::htmxRequestHeaders[]

//tag::outOfBandSwaps[]
        @Post
        HtmxResponse<?> outOfBandSwaps(@NonNull HtmxRequestHeaders htmxRequestHeaders) {
            HtmxResponse.builder()
                    .modelAndView(new ModelAndView<>("fruit", [fruit: new Fruit("Apple", "Red")]))
                    .modelAndView(new ModelAndView<>("swap", [:]))
                    .build()
        }
//end::outOfBandSwaps[]

//tag::htmxResponseHeaders[]
        @Get("/responseHeaders")
        HttpResponse<?> htmxResponseHeaders(@NonNull HtmxRequestHeaders htmxRequestHeaders) {
            HttpResponse.ok().header(HtmxResponseHeaders.HX_REFRESH, StringUtils.TRUE);
        }
//end::htmxResponseHeaders[]
    }
}

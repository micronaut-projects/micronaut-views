package io.micronaut.views.docs.htmx;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.docs.turbo.Fruit;
import io.micronaut.views.htmx.http.HtmxRequestHeaders;
import io.micronaut.views.htmx.http.HtmxResponse;
import io.micronaut.views.htmx.http.HtmxResponseHeaders;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@Property(name = "spec.name", value = "HtmxRequestHeadersTest")
@MicronautTest
class HtmxTest {

    @Test
    void testHtmxRequestHeaders(@Client("/") HttpClient httpClient) {
        BlockingHttpClient client = httpClient.toBlocking();
        String html = client.retrieve(HttpRequest.GET("/fruits/htmx").header("HX-Request", StringUtils.TRUE));
        assertEquals("""
                <h1>fruit: Apple</h1>
                <h2>color: Red</h2>
                """, html);
        html = client.retrieve(HttpRequest.GET("/fruits/htmx"));
        assertTrue(html.contains("<!DOCTYPE html>"));
    }

    @Test
    void testOutOfBandSwaps(@Client("/") HttpClient httpClient) {
        BlockingHttpClient client = httpClient.toBlocking();
        String html = client.retrieve(HttpRequest.POST("/fruits/htmx", Collections.emptyMap()).header("HX-Request", StringUtils.TRUE));
        assertEquals("""
                <h1>fruit: Apple</h1>
                <h2>color: Red</h2>
                <div id="message" hx-swap-oob="true">Swap me directly!</div>""", html);
    }

    @Test
    void htmxResponseHeaders(@Client("/") HttpClient httpClient) {
        BlockingHttpClient client = httpClient.toBlocking();
        HttpResponse<?> response = client.exchange(HttpRequest.GET("/fruits/htmx/responseHeaders").header("HX-Request", StringUtils.TRUE));
        assertEquals(StringUtils.TRUE, response.getHeaders().get("HX-Refresh"));
    }

    @Requires(property = "spec.name", value = "HtmxRequestHeadersTest")
    @Controller("/fruits/htmx")
    static class HtmxRequestHeadersController {
//tag::htmxRequestHeaders[]
        @Get
        ModelAndView<Map<String, Object>> index(@Nullable HtmxRequestHeaders htmxRequestHeaders) {
            Map<String, Object> model = Collections.singletonMap("fruit", new Fruit("Apple", "Red"));
            if (htmxRequestHeaders != null) {
                return new ModelAndView<>("fruit", model);
            }
            return new ModelAndView<>("fruits", model);
        }
//end::htmxRequestHeaders[]
//tag::outOfBandSwaps[]
        @Post
        HtmxResponse<?> outOfBandSwaps(@NonNull HtmxRequestHeaders htmxRequestHeaders) {
            return HtmxResponse.builder()
                    .modelAndView(new ModelAndView<>("fruit", Collections.singletonMap("fruit", new Fruit("Apple", "Red"))))
                    .modelAndView(new ModelAndView<>("swap", Collections.emptyMap()))
                    .build();
        }
//end::outOfBandSwaps[]

//tag::htmxResponseHeaders[]
        @Get("/responseHeaders")
        HttpResponse<?> htmxResponseHeaders(@NonNull HtmxRequestHeaders htmxRequestHeaders) {
            return HttpResponse.ok().header(HtmxResponseHeaders.HX_REFRESH, StringUtils.TRUE);
        }
//end::htmxResponseHeaders[]
    }
}

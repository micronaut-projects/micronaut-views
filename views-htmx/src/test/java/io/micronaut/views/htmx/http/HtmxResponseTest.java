package io.micronaut.views.htmx.http;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.*;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ModelAndView;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @see <a href="https://htmx.org/attributes/hx-swap-oob/">hx-swap-oob</a>
 */
@MicronautTest
@Property(name = "spec.name", value = "HtmxResponseTest")
class HtmxResponseTest {
    @Test
    void testHxSwapOob(@Client("/") HttpClient httpClient) {
        BlockingHttpClient client = httpClient.toBlocking();
        HttpResponse<String> response = assertDoesNotThrow(() -> client.exchange(HttpRequest.GET("/hx-swap-oob"), String.class));
        assertEquals(HttpStatus.OK, response.getStatus());
        Optional<String> htmlOptional = response.getBody();
        assertTrue(htmlOptional.isPresent());
        String html = htmlOptional.get();
        assertEquals("""
                <div>
                    Hello World
                </div><div id="alerts" hx-swap-oob="true">
                    Saved!
                </div>""", html);
    }

    @Requires(property = "spec.name", value = "HtmxResponseTest")
    @Controller
    static class HxSwapOobController {
        @Get("/hx-swap-oob")
        HtmxResponse<Map<String, Object>> hxSwapOob() {
            return HtmxResponse.<Map<String, Object>>builder()
                    .modelAndView(new ModelAndView<>("helloWorld", Collections.emptyMap()))
                    .modelAndView(new ModelAndView<>("alerts", Collections.emptyMap()))
                    .build();
        }

    }
}
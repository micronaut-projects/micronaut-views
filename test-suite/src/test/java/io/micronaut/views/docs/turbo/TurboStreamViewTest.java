package io.micronaut.views.docs.turbo;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.views.turbo.TurboStreamAction;
import io.micronaut.views.turbo.TurboStreamView;
import io.micronaut.views.turbo.http.TurboHttpHeaders;
import io.micronaut.views.turbo.http.TurboMediaType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TurboStreamViewTest {

    @Test
    void turboView() throws IOException {
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class,
            Map.of(
                "micronaut.views.soy.enabled", StringUtils.FALSE,
                "spec.name", "TurboViewTest",
                "micronaut.security.enabled", StringUtils.FALSE
            )
        );
        HttpClient httpClient = embeddedServer.getApplicationContext().createBean(HttpClient.class, embeddedServer.getURL());
        BlockingHttpClient client = httpClient.toBlocking();

        //tag::turboviewrequest[]
        HttpRequest<?> request = HttpRequest.GET("/turbofruit")
            .accept(TurboMediaType.TURBO_STREAM, MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML)
            .header(TurboHttpHeaders.TURBO_FRAME, "dom_id");
        //end::turboviewrequest[]

        HttpResponse<String> response = client.exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.status());
        assertTrue(response.getContentType().isPresent());
        assertEquals(TurboMediaType.TURBO_STREAM, response.getContentType().get().toString());
        assertEquals(
        //tag::turboviewresponse[]
            "<turbo-stream action=\"append\" target=\"dom_id\">"+
                "<template>" +
                    "<h1>fruit: Banana</h1>\n" +
                    "<h2>color: Yellow</h2>\n" +
                "</template>" +
            "</turbo-stream>"
        //end::turboviewresponse[]
            , response.body());
        httpClient.close();
        embeddedServer.close();
    }

    @Requires(property = "spec.name", value = "TurboViewTest")
    @Controller
    static class FruitController {

        //tag::turboview[]
        @Produces(value = {MediaType.TEXT_HTML, TurboMediaType.TURBO_STREAM})
        @TurboStreamView(value = "fruit", action = TurboStreamAction.APPEND)
        @Get("/turbofruit")
        Map<String, Object> show() {
            return Collections.singletonMap("fruit", new Fruit("Banana", "Yellow"));
        }
        //end::turboview[]
    }
}

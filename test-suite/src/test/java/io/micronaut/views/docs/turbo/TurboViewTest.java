package io.micronaut.views.docs.turbo;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.io.Writable;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpHeaders;
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
import io.micronaut.views.View;
import io.micronaut.views.turbo.TurboStream;
import io.micronaut.views.turbo.TurboStreamAction;
import io.micronaut.views.turbo.TurboStreamRenderer;
import io.micronaut.views.turbo.TurboView;
import io.micronaut.views.turbo.http.TurboHttpHeaders;
import io.micronaut.views.turbo.http.TurboMediaType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TurboViewTest {

    @Test
    void turboView()  throws IOException {
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class,
                CollectionUtils.mapOf("micronaut.views.soy.enabled", StringUtils.FALSE,
                "spec.name", "TurboViewTest",
                        "micronaut.security.enabled", StringUtils.FALSE));
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
        "<h2>color: Yellow</h2>" +
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
@Produces(MediaType.TEXT_HTML)
@TurboView(value = "fruit", action = TurboStreamAction.APPEND)
@Get("/turbofruit")
Map<String, Object> show() {
 return Collections.singletonMap("fruit", new TurboStreamTemplateTest.Fruit("Banana", "Yellow"));
 }
//end::turboview[]
    }

    @Introspected
    public static class Fruit {
        private final String name;
        private final String color;

        public Fruit(String name, String color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public String getColor() {
            return color;
        }
    }
}

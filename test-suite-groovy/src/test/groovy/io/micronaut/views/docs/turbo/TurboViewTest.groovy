package io.micronaut.views.docs.turbo

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.Introspected
import io.micronaut.core.util.CollectionUtils
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.views.turbo.TurboStreamAction
import io.micronaut.views.turbo.TurboView
import io.micronaut.views.turbo.http.TurboHttpHeaders
import io.micronaut.views.turbo.http.TurboMediaType
import spock.lang.Specification

class TurboViewTest extends Specification {

    void "you can use TurboView annotation"()  throws IOException {
        given:
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class,
                CollectionUtils.mapOf("micronaut.views.soy.enabled", StringUtils.FALSE,
                "spec.name", "TurboViewTest",
                        "micronaut.security.enabled", StringUtils.FALSE));
        HttpClient httpClient = embeddedServer.getApplicationContext().createBean(HttpClient.class, embeddedServer.getURL());
        BlockingHttpClient client = httpClient.toBlocking();

        when:
//tag::turboviewrequest[]
HttpRequest<?> request = HttpRequest.GET("/turbofruit")
        .header(TurboHttpHeaders.TURBO_FRAME, "dom_id");
//end::turboviewrequest[]
        HttpResponse<String> response = client.exchange(request, String.class);

        then:
        HttpStatus.OK == response.status()
        response.getContentType().isPresent()
        TurboMediaType.TURBO_STREAM == response.getContentType().get().toString()
//tag::turboviewresponse[]
"<turbo-stream action=\"append\" target=\"dom_id\">"+
    "<template>" +
        "<h1>fruit: Banana</h1>\n" +
        "<h2>color: Yellow</h2>" +
    "</template>" +
"</turbo-stream>"
//end::turboviewresponse[]
                == response.body()
        cleanup:
        httpClient.close()
        embeddedServer.close()
    }

    @Requires(property = "spec.name", value = "TurboViewTest")
    @Controller
    static class FruitController {

//tag::turboview[]
@TurboView(value = "fruit", action = TurboStreamAction.APPEND)
@Get("/turbofruit")
Map<String, Object> show() {
 return Collections.singletonMap("fruit", new Fruit("Banana", "Yellow"));
 }
//end::turboview[]
    }

    @Introspected
    static class Fruit {
        private final String name;
        private final String color;

        Fruit(String name, String color) {
            this.name = name;
            this.color = color;
        }

        String getName() {
            return name;
        }

        String getColor() {
            return color;
        }
    }
}

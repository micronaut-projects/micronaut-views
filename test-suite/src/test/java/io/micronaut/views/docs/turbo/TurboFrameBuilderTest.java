package io.micronaut.views.docs.turbo;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.turbo.TurboFrame;
import io.micronaut.views.turbo.http.TurboHttpHeaders;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@Property(name = "spec.name", value = "TurboFrameBuilderTest")
@MicronautTest
public class TurboFrameBuilderTest {
    @Inject
    @Client("/")
    HttpClient httpClient;

    @Test
    void youCanUseTurboFrameToRenderFrame() {
        BlockingHttpClient client = httpClient.toBlocking();

        String html = client.retrieve(HttpRequest.GET("/frame/builder").accept(MediaType.TEXT_HTML), String.class);
        assertTrue(html.contains("<h1>Editing message</h1>"));
        assertTrue(html.contains("<turbo-frame id=\"message_1\">"));
        assertTrue(html.contains("<form action=\"/messages/1\">"));

        html = client.retrieve(HttpRequest.GET("/frame/builder")
                .accept(MediaType.TEXT_HTML)
                .header(TurboHttpHeaders.TURBO_FRAME, "message_1"), String.class);
        assertFalse(html.contains("<h1>Editing message</h1>"));
        assertTrue(html.contains("<turbo-frame id=\"message_1\">"));
        assertTrue(html.contains("<form action=\"/messages/1\">"));
    }

    @Requires(property = "spec.name", value = "TurboFrameBuilderTest")
    @Controller("/frame")
    static class TurboFrameController {
//tag::turboFrameBuilder[]
@Produces(MediaType.TEXT_HTML)
@Get("/builder")
HttpResponse<?> index(@Nullable @Header(TurboHttpHeaders.TURBO_FRAME) String turboFrame) {
    Long messageId = 1L;
    Map<String, Object> model = Collections.singletonMap("message",
            new Message(messageId, "My message title", "My message content"));
    return HttpResponse.ok(turboFrame == null ?
                    new ModelAndView("edit", model) :
                    TurboFrame.builder()
                            .id("message_"  + messageId)
                            .templateModel(model)
                            .templateView("form"))
            .contentType(MediaType.TEXT_HTML);
}
//end::turboFrameBuilder[]
    }

    public static class Message {
        private final Long id;
        private final String name;
        private final String content;

        public Message(Long id, String name, String content) {
            this.id = id;
            this.name = name;
            this.content = content;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getContent() {
            return content;
        }
    }
}

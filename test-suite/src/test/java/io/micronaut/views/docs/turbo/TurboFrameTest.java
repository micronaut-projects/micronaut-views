package io.micronaut.views.docs.turbo;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Part;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.multipart.MultipartBody;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;
import io.micronaut.views.turbo.TurboFrame;
import io.micronaut.views.turbo.TurboFrameView;
import io.micronaut.views.turbo.TurboView;
import io.micronaut.views.turbo.http.TurboHttpHeaders;
import io.micronaut.views.turbo.http.TurboMediaType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@Property(name = "spec.name", value = "TurboFrameTest")
@MicronautTest
class TurboFrameTest {

    @Inject
    @Client("/")
    HttpClient httpClient;

    @Test
    void youCanUseTurboFrameToRenderFrame() {
        BlockingHttpClient client = httpClient.toBlocking();
        String html = client.retrieve(HttpRequest.GET("/frame").accept(MediaType.TEXT_HTML), String.class);
        assertTrue(html.contains("<h1>Editing message</h1>"));
        assertTrue(html.contains("<turbo-frame id=\"message_1\">"));
        assertTrue(html.contains("<form action=\"/messages/1\">"));

        html = client.retrieve(HttpRequest.GET("/frame")
            .accept(MediaType.TEXT_HTML)
            .header(TurboHttpHeaders.TURBO_FRAME, "message_1"), String.class);
        assertFalse(html.contains("<h1>Editing message</h1>"));
        assertTrue(html.contains("<turbo-frame id=\"message_1\">"));
        assertTrue(html.contains("<form action=\"/messages/1\">"));

        html = client.retrieve(HttpRequest.GET("/frame/builder").accept(MediaType.TEXT_HTML), String.class);
        assertTrue(html.contains("<h1>Editing message</h1>"));
        assertTrue(html.contains("<turbo-frame id=\"message_1\">"));
        assertTrue(html.contains("<form action=\"/messages/1\">"));

        html = client.retrieve(HttpRequest.GET("/frame/builder")
            .accept(MediaType.TEXT_HTML)
            .header(TurboHttpHeaders.TURBO_FRAME, "message_1"), String.class);
        assertFalse(html.contains("<h1>Editing message</h1>"));
        assertTrue(html.contains("<turbo-frame id=\"message_1\">"));
        assertTrue(html.contains("<form action=\"/messages/1\">"));

        MultipartBody body = MultipartBody.builder()
            .addPart("id", "1")
            .addPart("title", "My new title")
            .addPart("body", "My new body")
            .build();
        html = client.retrieve(
            HttpRequest.POST("/frame/messages/1", body)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(TurboMediaType.TURBO_STREAM),
            String.class);
        assertEquals("<turbo-stream action=\"update\"><template><h1>My new title</h1>\n" +
            "<p>My new body</p>\n" +
            "</template></turbo-stream>", html);
    }

    @Requires(property = "spec.name", value = "TurboFrameTest")
    @Controller("/frame")
    static class TurboFrameController {

        //tag::turboFrameView[]
        @Produces(MediaType.TEXT_HTML)
        @TurboFrameView("form")
        @View("edit")
        @Get
        Map<String, Object> index() {
            return Map.of("message", new Message(1L, "My message title", "My message content"));
        }
        //end::turboFrameView[]

        //tag::turboFramePost[]
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        @TurboView("view")
        @Produces(value = {MediaType.TEXT_HTML, TurboMediaType.TURBO_STREAM})
        @Post("/messages/{id}")
        Map<String, Object> processEdit(@Part int id, @Part String title, @Part String body) {
            // Process the posted data, and return the updated message
            return Map.of("message", new Message((long) id, title, body));
        }
        //end::turboFramePost[]

        @Produces(MediaType.TEXT_HTML)
        @Get("/builder")
        HttpResponse<?> index(@Nullable @Header(TurboHttpHeaders.TURBO_FRAME) String turboFrame) {
            Long messageId = 1L;
            Map<String, Object> model = Map.of("message", new Message(messageId, "My message title", "My message content"));
            return HttpResponse.ok(turboFrame == null ?
                    new ModelAndView<>("edit", model) :
                    TurboFrame.builder()
                        .id("message_" + messageId)
                        .templateModel(model)
                        .templateView("form"))
                .contentType(MediaType.TEXT_HTML);
        }
    }
}

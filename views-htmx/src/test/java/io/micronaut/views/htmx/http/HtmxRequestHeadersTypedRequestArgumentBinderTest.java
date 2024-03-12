package io.micronaut.views.htmx.http;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.*;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ModelAndView;
import jakarta.inject.Singleton;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;
import spock.lang.Specification;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
@Property(name = "spec.name", value = "HtmxRequestHeadersTypedRequestArgumentBinderTest")
class HtmxRequestHeadersTypedRequestArgumentBinderTest {

    @Test
    void testHtmxRequestHeadersIsBound(@Client("/") HttpClient httpClient) {
        BlockingHttpClient client = httpClient.toBlocking();

        Map<String, String> headers = new HashMap<>();
        headers.put("Host", "localhost:8080");
        headers.put("Connection", "keep-alive");
        headers.put("Content-Length", "23");
        headers.put("sec-ch-ua", "Chromium\";v=\"122\", \"Not(A:Brand\";v=\"24\", \"Google Chrome\";v=\"122");
        headers.put("HX-Target", "todo-list");
        headers.put("HX-Current-URL", "http://localhost:8080/");
        headers.put("HX-Trigger-Name", "title");
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
        headers.put("sec-ch-ua-mobile", "?0");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("HX-Trigger", "new-todo-input");
        headers.put("HX-Request", "true");
        headers.put("sec-ch-ua-platform", "macOS");
        headers.put("Accept", "*/*");
        headers.put("Origin", "http://localhost:8080");
        headers.put("Sec-Fetch-Site", "same-origin");
        headers.put("Sec-Fetch-Mode", "cors");
        headers.put("Sec-Fetch-Dest", "empty");
        headers.put("Referer", "http://localhost:8080/");
        headers.put("Accept-Encoding", "gzip, deflate, br, zstd");
        headers.put("Accept-Language", "en-GB,en-US;q=0.9,en;q=0.8");

        MutableHttpRequest<?> mutableHttpRequest = HttpRequest.POST(UriBuilder.of("/todo").path("save").build(), Map.of("title", "Learn HTMX"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            mutableHttpRequest.header(entry.getKey(), entry.getValue());
        }
        HttpResponse<?> response = assertDoesNotThrow(() -> client.exchange(mutableHttpRequest));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("itemAdded", response.getHeaders().get("HX-Trigger"));
    }

    //@Requires(property = "spec.name", value = "HtmxRequestHeadersTypedRequestArgumentBinderTest")
    @Controller("/todo")
    static class TodoItemController {
        private static final String MODEL_ITEM = "item";
        private final TodoItemRepository repository;
        private final TodoItemMapper todoItemMapper;

        TodoItemController(TodoItemRepository repository,
                           TodoItemMapper todoItemMapper) {
            this.repository = repository;
            this.todoItemMapper = todoItemMapper;
        }

        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        @Post("/save")
        HttpResponse<?> addNewTodoItem(@Body TodoItemFormData formData,
                                       @Nullable HtmxRequestHeaders htmxRequestHeaders) {
            TodoItem item = repository.save(todoItemMapper.toEntity(formData));
            if (htmxRequestHeaders != null) {
                return HttpResponse.ok(new ModelAndView<>("fragments", Collections.singletonMap(MODEL_ITEM, item)))
                        .header(HtmxResponseHeaders.HX_TRIGGER, "itemAdded");
            }
            return HttpResponse.seeOther(URI.create("/"));
        }
    }

    @Requires(property = "spec.name", value = "HtmxRequestHeadersTypedRequestArgumentBinderTest")
    @Singleton
    static class TodoItemRepository {
        @NonNull
        public TodoItem save(@NonNull TodoItem entity) {
            return entity;
        }
    }
}
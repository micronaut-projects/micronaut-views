package io.micronaut.views.htmx.http;

import io.micronaut.core.convert.ConversionService;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.simple.SimpleHttpHeaders;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class HtmxRequestHeadersTest {
    @Test
    void testHtmxRequestHeaders() {
        assertEquals("HX-Boosted", HtmxRequestHeaders.HX_BOOSTED);
        assertEquals("HX-Current-URL", HtmxRequestHeaders.HX_CURRENT_URL);
        assertEquals("HX-History-Restore-Request", HtmxRequestHeaders.HX_HISTORY_RESTORE_REQUEST);
        assertEquals("HX-Prompt", HtmxRequestHeaders.HX_PROMPT);
        assertEquals("HX-Request", HtmxRequestHeaders.HX_REQUEST);
        assertEquals("HX-Target", HtmxRequestHeaders.HX_TARGET);
        assertEquals("HX-Trigger-Name", HtmxRequestHeaders.HX_TRIGGER_NAME);
        assertEquals("HX-Trigger", HtmxRequestHeaders.HX_TRIGGER);
    }

    @Test
    void instantiationOfRequest(ConversionService conversionService) {
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
        HttpHeaders httpHeaders = new SimpleHttpHeaders(headers, conversionService);
        Optional<HtmxRequestHeaders> htmxRequestHeadersOptional = HtmxRequestHeaders.of(httpHeaders);
        assertTrue(htmxRequestHeadersOptional.isPresent());
        HtmxRequestHeaders htmxRequestHeaders = htmxRequestHeadersOptional.get();
        assertFalse(htmxRequestHeaders.isBoost());
        assertNull(htmxRequestHeaders.getHistoryRestoreRequest());
        assertEquals("http://localhost:8080/", htmxRequestHeaders.getCurrentUrl());
        assertNull(htmxRequestHeaders.getPrompt());
        assertEquals("todo-list", htmxRequestHeaders.getTarget());
        assertEquals( "title", htmxRequestHeaders.getTriggerName());
        assertEquals("new-todo-input", htmxRequestHeaders.getTrigger());
    }

}
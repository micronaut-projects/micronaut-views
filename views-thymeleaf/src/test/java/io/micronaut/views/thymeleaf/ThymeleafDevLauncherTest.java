package io.micronaut.views.thymeleaf;

import io.micronaut.context.ApplicationContext;
import io.micronaut.dev.tck.ReloadHarness;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A tiny Thymeleaf application started by the development launcher, {@code MicronautDevMain}, whose template is
 * edited in its source directory while it runs.
 */
class ThymeleafDevLauncherTest {

    @TempDir
    Path project;

    @Test
    void anEditedTemplateIsRenderedByTheRunningGeneration() throws Exception {
        try (ReloadHarness harness = ReloadHarness.inDirectory(project)) {
            harness.manifest("resources.views", "src/main/resources/views")
                .property("micronaut.server.port", "-1")
                .source("example.PageController", """
                    package example;

                    import io.micronaut.http.annotation.Controller;
                    import io.micronaut.http.annotation.Get;
                    import io.micronaut.views.View;
                    import java.util.Map;

                    @Controller("/launcher")
                    public class PageController {
                        @View("launcher/page")
                        @Get
                        Map<String, Object> page() {
                            return Map.of("name", "Sergio");
                        }
                    }
                    """)
                .resource("views/launcher/page.html", "<p th:text=\"'one ' + ${name}\"></p>");
            ApplicationContext started = harness.start();
            URI uri = awaitServer(started.getBean(EmbeddedServer.class)).getURI().resolve("/launcher");
            assertTrue(get(uri).contains("one Sergio"));

            harness.resource("views/launcher/page.html", "<p th:text=\"'two ' + ${name}\"></p>");
            ApplicationContext reloaded = harness.reload();
            // no class changed: the same generation renders the edit
            assertEquals(1, harness.generation());
            assertSame(started, reloaded);
            assertTrue(get(uri).contains("two Sergio"));
        }
    }

    private static EmbeddedServer awaitServer(EmbeddedServer server) throws InterruptedException {
        // the launcher hands over the context once it started; the application starts its server right after
        long deadline = System.nanoTime() + Duration.ofSeconds(30).toNanos();
        while (!server.isRunning() && System.nanoTime() < deadline) {
            Thread.sleep(20);
        }
        assertTrue(server.isRunning(), "The server did not start");
        return server;
    }

    private static String get(URI uri) throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<String> response = client.send(HttpRequest.newBuilder(uri).build(), HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), response.body());
            return response.body();
        }
    }
}

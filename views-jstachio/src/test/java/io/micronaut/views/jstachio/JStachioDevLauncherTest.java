package io.micronaut.views.jstachio;

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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A tiny JStachio application started by the development launcher, {@code MicronautDevMain}. Its template is
 * compiled into a class by the annotation processor, so an edit of it is a class change: the next generation
 * renders it, with nothing to watch in views.
 */
class JStachioDevLauncherTest {

    private static final String PAGE = """
        package example;

        import io.jstach.jstache.JStache;

        @JStache(template = "<p>%s {{name}}</p>")
        public record Page(String name) {
        }
        """;

    @TempDir
    Path project;

    @Test
    void anEditedTemplateIsRenderedByTheNextGeneration() throws Exception {
        try (ReloadHarness harness = ReloadHarness.inDirectory(project)) {
            harness.property("micronaut.server.port", "-1")
                .source("example.Page", PAGE.formatted("one"))
                .source("example.PageController", """
                    package example;

                    import io.micronaut.http.MediaType;
                    import io.micronaut.http.annotation.Controller;
                    import io.micronaut.http.annotation.Get;
                    import io.micronaut.http.annotation.Produces;

                    @Controller("/launcher")
                    public class PageController {
                        @Produces(MediaType.TEXT_HTML)
                        @Get
                        Page page() {
                            return new Page("Sergio");
                        }
                    }
                    """);
            ApplicationContext started = harness.start();
            URI uri = awaitServer(started.getBean(EmbeddedServer.class)).getURI().resolve("/launcher");
            assertTrue(get(uri).contains("one Sergio"));

            harness.source("example.Page", PAGE.formatted("two"));
            ApplicationContext next = harness.reload();
            assertEquals(2, harness.generation());
            uri = awaitServer(next.getBean(EmbeddedServer.class)).getURI().resolve("/launcher");
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

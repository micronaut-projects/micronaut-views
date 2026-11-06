package io.micronaut.views.jinjava;

import io.micronaut.dev.tck.ReloadHarness;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.beans.Introspector;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A tiny Jinjava application started by the development launcher: a generation that rendered a template, reading a class
 * of its own through the EL resolver, is collected once it retired.
 */
class JinjavaGenerationMemoryTest {

    @TempDir
    Path project;

    @Test
    void aRetiredGenerationThatRenderedATemplateIsCollected() throws Exception {
        try (ReloadHarness harness = ReloadHarness.inDirectory(project)) {
            harness.manifest("resources.views", "src/main/resources/views")
                .manifest("patch-in-place", "false")
                .property("micronaut.server.port", "-1")
                .source("example.Book", """
                    package example;

                    public class Book {
                        public String getTitle() {
                            return "Dune";
                        }
                    }
                    """)
                .source("example.PageController", controller("one"))
                .resource("views/generation/page.jinja", "<p>{{ edition }} {{ book.title }}</p>");
            harness.start();
            render(harness, "one Dune");

            // what the first use of a library in this JVM initializes may keep the first generation: the second is looked at
            harness.source("example.PageController", controller("two"));
            harness.reload();
            render(harness, "two Dune");
            WeakReference<ClassLoader> second = new WeakReference<>(harness.context().getClassLoader());

            harness.source("example.PageController", controller("three"));
            harness.reload();
            render(harness, "three Dune");
            assertEquals(3, harness.generation());

            assertCollected(second);
        }
    }

    private static String controller(String edition) {
        return """
            package example;

            import io.micronaut.http.annotation.Controller;
            import io.micronaut.http.annotation.Get;
            import io.micronaut.views.View;
            import java.util.Map;

            @Controller("/generation")
            public class PageController {
                @View("generation/page")
                @Get
                Map<String, Object> page() {
                    return Map.of("edition", "%s", "book", new Book());
                }
            }
            """.formatted(edition);
    }

    private static void render(ReloadHarness harness, String expected) throws InterruptedException, IOException {
        EmbeddedServer server = harness.context().getBean(EmbeddedServer.class);
        long deadline = System.nanoTime() + Duration.ofSeconds(30).toNanos();
        while (!server.isRunning() && System.nanoTime() < deadline) {
            Thread.sleep(20);
        }
        URI uri = server.getURI().resolve("/generation");
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<String> response = client.send(HttpRequest.newBuilder(uri).build(), HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), response.body());
            assertTrue(response.body().contains(expected), response.body());
        }
    }

    private static void assertCollected(WeakReference<ClassLoader> generation) throws InterruptedException {
        long deadline = System.nanoTime() + Duration.ofSeconds(30).toNanos();
        while (generation.get() != null && System.nanoTime() < deadline) {
            // what the JDK caches of the classes the resolver reads is the runtime's to forget, not this module's
            Introspector.flushCaches();
            Thread.ofVirtual().start(Introspector::flushCaches).join();
            System.gc(); // NOSONAR only a collection tells
            Thread.sleep(200);
        }
        assertNull(generation.get(), "The retired generation is still reachable");
    }
}

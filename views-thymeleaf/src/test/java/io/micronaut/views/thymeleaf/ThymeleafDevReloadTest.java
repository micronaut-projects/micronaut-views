package io.micronaut.views.thymeleaf;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.DefaultBeanContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.DevelopmentMode;
import io.micronaut.context.reload.ResourceKind;
import io.micronaut.context.watch.ResourceChange;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.views.View;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThymeleafDevReloadTest {

    private static final String SPEC = "ThymeleafDevReloadTest";

    @TempDir
    Path root;

    @Test
    void inDevelopmentModeAnEditedTemplateIsRenderedOnTheNextRequestWithoutARestart() throws IOException {
        Path page = write("devreload/page.html", "<p th:text=\"'one ' + ${name}\"></p>");
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, Map.of(DevelopmentMode.PROPERTY, true, "spec.name", SPEC));
             HttpClient client = HttpClient.create(server.getURL())) {
            ApplicationContext context = server.getApplicationContext();
            // the launcher reports the startup state of its views roots
            notify(context, List.of(page), List.of(), true);
            assertTrue(get(client, "/devreload/page").contains("one Sergio"));

            // the template is read from the source root, and the engine's cache holds it until the change arrives
            Files.writeString(page, "<p th:text=\"'two ' + ${name}\"></p>");
            assertTrue(get(client, "/devreload/page").contains("one Sergio"));
            notify(context, List.of(page), List.of(), false);
            assertTrue(get(client, "/devreload/page").contains("two Sergio"));
        }
    }

    @Test
    void inDevelopmentModeAnEditedLayoutOfAnotherExtensionIsSeenByThePagesThatUseIt() throws IOException {
        Path layout = write("devreload/layout.xml", "<div th:fragment=\"banner\">first banner</div>");
        Path page = write("devreload/layout-page.html", "<main><div th:replace=\"~{devreload/layout.xml :: banner}\"></div></main>");
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, Map.of(DevelopmentMode.PROPERTY, true, "spec.name", SPEC));
             HttpClient client = HttpClient.create(server.getURL())) {
            ApplicationContext context = server.getApplicationContext();
            notify(context, List.of(layout, page), List.of(), true);
            assertTrue(get(client, "/devreload/layout-page").contains("first banner"));
            // a view named with an extension of its own is found where the resolver reads it
            assertTrue(context.getBean(ThymeleafViewsRenderer.class).exists("devreload/layout.xml"));

            Files.writeString(layout, "<div th:fragment=\"banner\">second banner</div>");
            notify(context, List.of(layout), List.of(), false);
            assertTrue(get(client, "/devreload/layout-page").contains("second banner"));
        }
    }

    @Test
    void outsideDevelopmentModeTemplatesAreReadFromTheClassPathAndNothingIsWatched() throws IOException {
        // a source root holding a different home page is configured, and ignored outside development mode
        Path home = write("home.html", "<p>from the source root</p>");
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, Map.of("spec.name", SPEC,
                "micronaut.views.source-roots", List.of(root.toString())));
             HttpClient client = HttpClient.create(server.getURL())) {
            ApplicationContext context = server.getApplicationContext();
            ThymeleafViewsRenderer<?> renderer = context.getBean(ThymeleafViewsRenderer.class);
            assertFalse(renderer.templateResolver instanceof SourceRootsTemplateResolver);
            assertFalse(renderer.exists("devreload/page"));

            String before = get(client, "/devreload/home");
            assertTrue(before.contains("username: <span>sdelamo</span>"), before);
            notify(context, List.of(home), List.of(), true);
            notify(context, List.of(home), List.of(), false);
            assertEquals(before, get(client, "/devreload/home"));
        }
    }

    @Test
    void theRendererWatchesItsSuffixOnlyWhenTheResolverForcesIt() {
        try (ApplicationContext context = ApplicationContext.run(Map.of(DevelopmentMode.PROPERTY, true))) {
            assertEquals(Set.of(), context.getBean(ThymeleafViewsRenderer.class).extensions());
        }
        try (ApplicationContext context = ApplicationContext.run(Map.of(DevelopmentMode.PROPERTY, true, "micronaut.views.thymeleaf.force-suffix", true))) {
            assertEquals(Set.of("html"), context.getBean(ThymeleafViewsRenderer.class).extensions());
        }
    }

    private Path write(String name, String content) throws IOException {
        Path file = root.resolve(name);
        Files.createDirectories(file.getParent());
        Files.writeString(file, content);
        return file;
    }

    private void notify(ApplicationContext context, List<Path> changed, List<Path> removed, boolean initial) {
        ((DefaultBeanContext) context).notifyResourceChange(new ResourceChange(ResourceKind.VIEWS, List.of(root), changed, removed, initial));
    }

    private static String get(HttpClient client, String path) {
        return client.toBlocking().retrieve(path);
    }

    @Requires(property = "spec.name", value = SPEC)
    @Controller("/devreload")
    static class DevReloadController {

        @View("devreload/page")
        @Get("/page")
        Map<String, Object> page() {
            return Map.of("name", "Sergio");
        }

        @View("devreload/layout-page")
        @Get("/layout-page")
        Map<String, Object> layoutPage() {
            return Map.of();
        }

        @View("home")
        @Get("/home")
        Map<String, Object> home() {
            return Map.of("loggedIn", true, "username", "sdelamo");
        }
    }
}

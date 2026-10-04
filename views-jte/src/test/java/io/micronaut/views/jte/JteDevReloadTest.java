package io.micronaut.views.jte;

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
import io.micronaut.views.ViewsSourceRoots;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JteDevReloadTest {

    private static final String SPEC = "JteDevReloadTest";

    @TempDir
    Path root;

    @TempDir
    Path classes;

    @Test
    void inDevelopmentModeAnEditedTemplateIsCompiledAgainOnTheNextRequestWithoutARestart() throws IOException {
        Path page = write("devreload/page.jte", "@param String name\n<p>one ${name}</p>");
        try (EmbeddedServer server = run(Map.of(DevelopmentMode.PROPERTY, true));
             HttpClient client = HttpClient.create(server.getURL())) {
            ApplicationContext context = server.getApplicationContext();
            notify(context, List.of(page), List.of(), true);
            assertTrue(get(client, "/devreload/page").contains("one Sergio"));

            // compiled from the source root, and compiled again only once the change arrives
            Files.writeString(page, "@param String name\n<p>two ${name}</p>");
            assertTrue(get(client, "/devreload/page").contains("one Sergio"));
            notify(context, List.of(page), List.of(), false);
            assertTrue(get(client, "/devreload/page").contains("two Sergio"));
        }
    }

    @Test
    void inDevelopmentModeAnEditedLayoutIsSeenByThePagesThatUseIt() throws IOException {
        Path layout = write("devreload/layout.jte", "@param gg.jte.Content content\n<header>first header</header>${content}");
        Path child = write("devreload/child.jte", "@template.devreload.layout(content = @`<p>child</p>`)");
        try (EmbeddedServer server = run(Map.of(DevelopmentMode.PROPERTY, true));
             HttpClient client = HttpClient.create(server.getURL())) {
            ApplicationContext context = server.getApplicationContext();
            notify(context, List.of(layout, child), List.of(), true);
            assertTrue(get(client, "/devreload/child").contains("first header"));

            Files.writeString(layout, "@param gg.jte.Content content\n<header>second header</header>${content}");
            notify(context, List.of(layout), List.of(), false);
            String body = get(client, "/devreload/child");
            assertTrue(body.contains("second header") && body.contains("child"), body);
        }
    }

    @Test
    void inDevelopmentModeATemplateOutsideTheSourceRootsIsRenderedFromItsPrecompiledClass() {
        try (EmbeddedServer server = run(Map.of(DevelopmentMode.PROPERTY, true));
             HttpClient client = HttpClient.create(server.getURL())) {
            ApplicationContext context = server.getApplicationContext();
            notify(context, List.of(), List.of(), true);
            assertTrue(context.getBean(ViewsSourceRoots.class).isEnabled());
            String body = get(client, "/devreload/home");
            assertTrue(body.contains("username: <span>sdelamo</span>"), body);
            // nothing was compiled at runtime
            assertFalse(Files.exists(classes.resolve("html").resolve("gg")));
        }
    }

    @Test
    void outsideDevelopmentModeTemplatesArePrecompiledAndNothingIsWatched() throws IOException {
        Path home = write("home.jte", "<p>from the source root</p>");
        write("devreload/page.jte", "@param String name\n<p>one ${name}</p>");
        try (EmbeddedServer server = run(Map.of("micronaut.views.source-roots", List.of(root.toString())));
             HttpClient client = HttpClient.create(server.getURL())) {
            ApplicationContext context = server.getApplicationContext();
            assertFalse(context.getBean(ViewsSourceRoots.class).isEnabled());
            assertFalse(context.getBean(HtmlJteViewsRenderer.class).exists("devreload/page"));

            String before = get(client, "/devreload/home");
            assertTrue(before.contains("username: <span>sdelamo</span>"), before);
            notify(context, List.of(home), List.of(), true);
            notify(context, List.of(home), List.of(), false);
            assertEquals(before, get(client, "/devreload/home"));
        }
    }

    @Test
    @SuppressWarnings("deprecation")
    void theDeprecatedDynamicModeStillFollowsTheTimeOfTheFilesOutsideDevelopmentMode() throws IOException {
        Path page = write("devreload/page.jte", "@param String name\n<p>one ${name}</p>");
        try (EmbeddedServer server = run(Map.of("micronaut.views.jte.dynamic", true,
                "micronaut.views.jte.dynamic-source-path", root.toString()));
             HttpClient client = HttpClient.create(server.getURL())) {
            assertTrue(server.getApplicationContext().getBean(JteViewsRendererConfiguration.class).isDynamic());
            assertTrue(get(client, "/devreload/page").contains("one Sergio"));

            Files.writeString(page, "@param String name\n<p>two ${name}</p>");
            Files.setLastModifiedTime(page, FileTime.from(Instant.now().plusSeconds(10)));
            assertTrue(get(client, "/devreload/page").contains("two Sergio"));
        }
    }

    @Test
    void inDevelopmentModeTheDeprecatedDynamicSourcePathIsReadAfterTheSourceRoots(@TempDir Path dynamicSource) throws IOException {
        Path page = dynamicSource.resolve("devreload/page.jte");
        Files.createDirectories(page.getParent());
        Files.writeString(page, "@param String name\n<p>dynamic ${name}</p>");
        try (EmbeddedServer server = run(Map.of(DevelopmentMode.PROPERTY, true,
                "micronaut.views.jte.dynamic-source-path", dynamicSource.toString()));
             HttpClient client = HttpClient.create(server.getURL())) {
            ApplicationContext context = server.getApplicationContext();
            notify(context, List.of(), List.of(), true);
            assertTrue(get(client, "/devreload/page").contains("dynamic Sergio"));

            // the source roots come first
            Path source = write("devreload/page.jte", "@param String name\n<p>source ${name}</p>");
            notify(context, List.of(source), List.of(), false);
            String moved = get(client, "/devreload/page");
            assertTrue(moved.contains("source Sergio"), moved);
        }
    }

    private EmbeddedServer run(Map<String, Object> properties) {
        Map<String, Object> all = new HashMap<>(properties);
        all.put("spec.name", SPEC);
        all.put("micronaut.views.jte.dynamic-path", classes.toString());
        return ApplicationContext.run(EmbeddedServer.class, all);
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

        @View("devreload/child")
        @Get("/child")
        Map<String, Object> child() {
            return Map.of();
        }

        @View("home")
        @Get("/home")
        Map<String, Object> home() {
            return Map.of("loggedIn", true, "username", "sdelamo");
        }
    }
}

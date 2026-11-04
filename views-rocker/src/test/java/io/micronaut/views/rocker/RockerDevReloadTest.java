package io.micronaut.views.rocker;

import com.fizzed.rocker.runtime.RockerRuntime;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.DefaultBeanContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.DevelopmentMode;
import io.micronaut.context.reload.ResourceKind;
import io.micronaut.context.watch.ResourceChange;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.views.View;
import io.micronaut.views.ViewsSourceRoots;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RockerDevReloadTest {

    private static final String SPEC = "RockerDevReloadTest";

    @TempDir
    Path root;

    @Test
    void inDevelopmentModeAnEditedTemplateIsCompiledAgainAfterTheChange() throws IOException {
        Path page = write("devreload/page.rocker.html", "@args (String name)\n<p>one @name</p>\n");
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, devProperties(Map.of()));
             HttpClient client = HttpClient.create(server.getURL())) {
            ApplicationContext context = server.getApplicationContext();
            notify(context, List.of(page), true);
            assertTrue(get(client, "/devreload/page").contains("one Sergio"));

            // compiled once, until the change arrives
            Files.writeString(page, "@args (String name)\n<p>two @name</p>\n");
            assertTrue(get(client, "/devreload/page").contains("one Sergio"));
            notify(context, List.of(page), false);
            assertTrue(get(client, "/devreload/page").contains("two Sergio"));
        }
    }

    @Test
    void inDevelopmentModeATemplateCallingAnEditedTemplateRendersItsNewVersion() throws IOException {
        Path header = write("devreload/header.rocker.html", "<header>first header</header>\n");
        Path child = write("devreload/child.rocker.html", "@views.devreload.header.template()<p>child</p>\n");
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, devProperties(Map.of()));
             HttpClient client = HttpClient.create(server.getURL())) {
            ApplicationContext context = server.getApplicationContext();
            notify(context, List.of(header, child), true);
            assertTrue(get(client, "/devreload/child").contains("first header"));

            Files.writeString(header, "<header>second header</header>\n");
            notify(context, List.of(header), false);
            String body = get(client, "/devreload/child");
            assertTrue(body.contains("second header") && body.contains("child"), body);
        }
    }

    @Test
    void inDevelopmentModeATemplateThatDoesNotCompileFailsToRenderUntilItCompiles() throws IOException {
        Path page = write("devreload/page.rocker.html", "@args (String name)\n<p>one @name</p>\n");
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, devProperties(Map.of()));
             HttpClient client = HttpClient.create(server.getURL())) {
            ApplicationContext context = server.getApplicationContext();
            notify(context, List.of(page), true);
            assertTrue(get(client, "/devreload/page").contains("one Sergio"));

            Files.writeString(page, "@args (String name)\n<p>@name.noSuchMethod()</p>\n");
            notify(context, List.of(page), false);
            assertThrows(HttpClientResponseException.class, () -> get(client, "/devreload/page"));
            assertTrue(context.getBean(RockerViewsRenderer.class).exists("devreload/page"));

            Files.writeString(page, "@args (String name)\n<p>three @name</p>\n");
            notify(context, List.of(page), false);
            assertTrue(get(client, "/devreload/page").contains("three Sergio"));
        }
    }

    @Test
    void inDevelopmentModeAPrecompiledTemplateIsRenderedAndRockersOwnReloadingStaysOff() throws IOException {
        write("devreload/page.rocker.html", "@args (String name)\n<p>one @name</p>\n");
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class,
                devProperties(Map.of("micronaut.views.rocker.hot-reloading", true)));
             HttpClient client = HttpClient.create(server.getURL())) {
            RockerRuntime.getInstance().setReloading(true);
            notify(server.getApplicationContext(), List.of(), true);
            String body = get(client, "/devreload/home");
            assertTrue(body.contains("username: <span>sdelamo</span>"), body);
            assertFalse(RockerRuntime.getInstance().isReloading());
        }
    }

    @Test
    void outsideDevelopmentModePrecompiledTemplatesAreUsedAndNothingIsCompiled() throws IOException {
        Path home = write("home.rocker.html", "@args (Boolean loggedIn, String username)\n<p>from the source root</p>\n");
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, Map.of("spec.name", SPEC,
                "micronaut.views.source-roots", List.of(root.toString())));
             HttpClient client = HttpClient.create(server.getURL())) {
            ApplicationContext context = server.getApplicationContext();
            assertFalse(context.getBean(ViewsSourceRoots.class).isEnabled());
            write("devreload/page.rocker.html", "@args (String name)\n<p>one @name</p>\n");
            assertFalse(context.getBean(RockerViewsRenderer.class).exists("devreload/page"));

            String before = get(client, "/devreload/home");
            assertTrue(before.contains("username: <span>sdelamo</span>"), before);
            notify(context, List.of(home), true);
            notify(context, List.of(home), false);
            assertEquals(before, get(client, "/devreload/home"));
        }
    }

    @Test
    void outsideDevelopmentModeTheDeprecatedHotReloadingTurnsOnRockersOwnReloading() {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, Map.of("spec.name", SPEC,
                "micronaut.views.rocker.hot-reloading", true));
             HttpClient client = HttpClient.create(server.getURL())) {
            String body = get(client, "/devreload/home");
            assertTrue(body.contains("username: <span>sdelamo</span>"), body);
            assertTrue(RockerRuntime.getInstance().isReloading());
        } finally {
            RockerRuntime.getInstance().setReloading(false);
        }
    }

    private Map<String, Object> devProperties(Map<String, Object> more) {
        Map<String, Object> properties = new HashMap<>(more);
        properties.put(DevelopmentMode.PROPERTY, true);
        properties.put("spec.name", SPEC);
        return properties;
    }

    private Path write(String name, String content) throws IOException {
        Path file = root.resolve(name);
        Files.createDirectories(file.getParent());
        Files.writeString(file, content);
        return file;
    }

    private void notify(ApplicationContext context, List<Path> changed, boolean initial) {
        ((DefaultBeanContext) context).notifyResourceChange(new ResourceChange(ResourceKind.VIEWS, List.of(root), changed, List.of(), initial));
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

package io.micronaut.views.jinjava;

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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JinjavaDevReloadTest {

    private static final String SPEC = "JinjavaDevReloadTest";

    @TempDir
    Path root;

    @Test
    void inDevelopmentModeATemplateIsReadFromTheSourceRootsAndParsedOnEveryRender() throws IOException {
        Path page = write("devreload/page.jinja", "<p>one {{ name }}</p>");
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, Map.of(DevelopmentMode.PROPERTY, true, "spec.name", SPEC));
             HttpClient client = HttpClient.create(server.getURL())) {
            ApplicationContext context = server.getApplicationContext();
            notify(context, List.of(page), List.of(), true);
            assertTrue(context.getBean(JinjavaViewsRenderer.class).exists("devreload/page"));
            assertTrue(get(client, "/devreload/page").contains("one Sergio"));

            // the engine keeps no compiled template: an edit is rendered on the next request
            Files.writeString(page, "<p>two {{ name }}</p>");
            assertTrue(get(client, "/devreload/page").contains("two Sergio"));
        }
    }

    @Test
    void inDevelopmentModeAnIncludedTemplateIsReadFromTheSourceRoots() throws IOException {
        Path header = write("devreload/layout.html", "<header>first header</header>{% block content %}{% endblock %}");
        write("devreload/child.jinja", "{% extends \"devreload/layout.html\" %}{% block content %}<p>child</p>{% endblock %}");
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, Map.of(DevelopmentMode.PROPERTY, true, "spec.name", SPEC));
             HttpClient client = HttpClient.create(server.getURL())) {
            notify(server.getApplicationContext(), List.of(), List.of(), true);
            assertTrue(get(client, "/devreload/child").contains("first header"));

            Files.writeString(header, "<header>second header</header>{% block content %}{% endblock %}");
            String body = get(client, "/devreload/child");
            assertTrue(body.contains("second header") && body.contains("child"), body);
        }
    }

    @Test
    void outsideDevelopmentModeTemplatesAreReadFromTheClassPath() throws IOException {
        Path home = write("home.jinja", "<p>from the source root</p>");
        write("devreload/page.jinja", "<p>one {{ name }}</p>");
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, Map.of("spec.name", SPEC,
                "micronaut.views.source-roots", List.of(root.toString())));
             HttpClient client = HttpClient.create(server.getURL())) {
            ApplicationContext context = server.getApplicationContext();
            assertFalse(context.getBean(ViewsSourceRoots.class).isEnabled());
            assertFalse(context.getBean(JinjavaViewsRenderer.class).exists("devreload/page"));

            String before = get(client, "/devreload/home");
            assertTrue(before.contains("username: <span>sdelamo</span>"), before);
            notify(context, List.of(home), List.of(), true);
            assertEquals(before, get(client, "/devreload/home"));
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

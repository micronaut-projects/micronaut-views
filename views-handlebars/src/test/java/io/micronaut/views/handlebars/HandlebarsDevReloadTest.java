package io.micronaut.views.handlebars;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.cache.ConcurrentMapTemplateCache;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.DefaultBeanContext;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.DevelopmentMode;
import io.micronaut.context.reload.ResourceKind;
import io.micronaut.context.watch.ResourceChange;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.views.View;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsSourceRoots;
import jakarta.inject.Singleton;
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

class HandlebarsDevReloadTest {

    private static final String SPEC = "HandlebarsDevReloadTest";
    private static final String CACHED = "HandlebarsDevReloadTest.cached";

    @TempDir
    Path root;

    @Test
    void inDevelopmentModeATemplateIsReadFromTheSourceRootsAndCompiledOnEveryRender() throws IOException {
        Path page = write("devreload/page.hbs", "<p>one {{name}}</p>");
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, Map.of(DevelopmentMode.PROPERTY, true, "spec.name", SPEC));
             HttpClient client = HttpClient.create(server.getURL())) {
            ApplicationContext context = server.getApplicationContext();
            notify(context, List.of(page), List.of(), true);
            assertTrue(context.getBean(HandlebarsViewsRenderer.class).exists("devreload/page"));
            assertTrue(get(client, "/devreload/page").contains("one Sergio"));

            // the default engine has no template cache: it compiles the template again on every render
            Files.writeString(page, "<p>two {{name}}</p>");
            assertTrue(get(client, "/devreload/page").contains("two Sergio"));
            notify(context, List.of(page), List.of(), false);
            assertTrue(get(client, "/devreload/page").contains("two Sergio"));
        }
    }

    @Test
    void inDevelopmentModeAnEngineWithACacheRendersAnEditOnTheNextRequestAfterTheChange() throws IOException {
        Path page = write("devreload/page.hbs", "<p>one {{name}}</p>");
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, Map.of(DevelopmentMode.PROPERTY, true, "spec.name", SPEC, CACHED, true));
             HttpClient client = HttpClient.create(server.getURL())) {
            ApplicationContext context = server.getApplicationContext();
            notify(context, List.of(page), List.of(), true);
            assertTrue(get(client, "/devreload/page").contains("one Sergio"));

            Files.writeString(page, "<p>two {{name}}</p>");
            assertTrue(get(client, "/devreload/page").contains("one Sergio"));
            notify(context, List.of(page), List.of(), false);
            assertTrue(get(client, "/devreload/page").contains("two Sergio"));
        }
    }

    @Test
    void outsideDevelopmentModeTemplatesAreReadFromTheClassPathAndNothingIsWatched() throws IOException {
        Path home = write("home.hbs", "<p>from the source root</p>");
        write("devreload/page.hbs", "<p>one {{name}}</p>");
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, Map.of("spec.name", SPEC,
                "micronaut.views.source-roots", List.of(root.toString())));
             HttpClient client = HttpClient.create(server.getURL())) {
            ApplicationContext context = server.getApplicationContext();
            assertFalse(context.getBean(ViewsSourceRoots.class).isEnabled());
            assertFalse(context.getBean(HandlebarsViewsRenderer.class).exists("devreload/page"));

            String before = get(client, "/devreload/home");
            assertTrue(before.contains("username: <span>sdelamo</span>"), before);
            notify(context, List.of(home), List.of(), true);
            notify(context, List.of(home), List.of(), false);
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

    @Factory
    @Requires(property = CACHED)
    static class CachedHandlebarsFactory {

        @Singleton
        @Replaces(Handlebars.class)
        Handlebars cachedHandlebars(ViewsConfiguration viewsConfiguration, ViewsSourceRoots sourceRoots, ClassPathResourceLoader resourceLoader) {
            return new HandlebarsFactory().handlebars(viewsConfiguration, sourceRoots, resourceLoader)
                .with(new ConcurrentMapTemplateCache());
        }
    }

    @Requires(property = "spec.name", value = SPEC)
    @Controller("/devreload")
    static class DevReloadController {

        @View("devreload/page")
        @Get("/page")
        Map<String, Object> page() {
            return Map.of("name", "Sergio");
        }

        @View("home")
        @Get("/home")
        Map<String, Object> home() {
            return Map.of("loggedIn", true, "username", "sdelamo");
        }
    }
}

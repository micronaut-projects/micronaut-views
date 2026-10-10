package io.micronaut.views.soy;

import com.google.template.soy.SoyFileSet;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SoyDevReloadTest {

    private static final String SPEC = "SoyDevReloadTest";
    private static final String PAGE = """
        {namespace devreload}
        {template page}
          {@param name: string}
          <p>%s {$name}</p>
        {/template}
        """;

    @TempDir
    Path root;

    @Test
    void inDevelopmentModeAnEditedFileSetIsCompiledAgainAfterTheChange() throws IOException {
        Path page = write("devreload.soy", PAGE.formatted("one"));
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, devProperties());
             HttpClient client = HttpClient.create(server.getURL())) {
            ApplicationContext context = server.getApplicationContext();
            assertTrue(get(client, "/devreload/page").contains("one Sergio"));

            // compiled once, until the change arrives
            Files.writeString(page, PAGE.formatted("two"));
            assertTrue(get(client, "/devreload/page").contains("one Sergio"));
            notify(context, List.of(page), false);
            assertTrue(get(client, "/devreload/page").contains("two Sergio"));
        }
    }

    @Test
    void inDevelopmentModeAFileSetThatDoesNotCompileFailsToRenderUntilItCompilesAgain() throws IOException {
        Path page = write("devreload.soy", PAGE.formatted("one"));
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, devProperties());
             HttpClient client = HttpClient.create(server.getURL())) {
            ApplicationContext context = server.getApplicationContext();
            assertTrue(get(client, "/devreload/page").contains("one Sergio"));

            Files.writeString(page, "{namespace devreload}\n{template page}\n  {if}\n{/template}\n");
            notify(context, List.of(page), false);
            assertThrows(HttpClientResponseException.class, () -> get(client, "/devreload/page"));

            Files.writeString(page, PAGE.formatted("three"));
            notify(context, List.of(page), false);
            assertTrue(get(client, "/devreload/page").contains("three Sergio"));

            // a file the provider adds is removed: the provider fails, and so does rendering, until it is back
            Files.delete(page);
            ((DefaultBeanContext) context).notifyResourceChange(new ResourceChange(ResourceKind.VIEWS, List.of(root), List.of(), List.of(page), false));
            assertThrows(HttpClientResponseException.class, () -> get(client, "/devreload/page"));
            Files.writeString(page, PAGE.formatted("four"));
            notify(context, List.of(page), false);
            assertTrue(get(client, "/devreload/page").contains("four Sergio"));
        }
    }

    @Test
    void outsideDevelopmentModeTheFileSetComesFromTheClassPathAndIsNotCompiledAgain() throws IOException {
        Path home = write("home.soy", "{namespace sample}\n{template home}\n  <p>from the source root</p>\n{/template}\n");
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, Map.of("spec.name", SPEC,
                "micronaut.views.source-roots", List.of(root.toString())));
             HttpClient client = HttpClient.create(server.getURL())) {
            ApplicationContext context = server.getApplicationContext();
            assertFalse(context.getBean(ViewsSourceRoots.class).isEnabled());
            assertTrue(context.getBean(SoyTemplateSources.class).resolve("home.soy").orElseThrow().getPath().endsWith("/views/home.soy"));
            assertFalse(context.getBean(SoyTemplateSources.class).resolve("home.soy").orElseThrow().getPath().startsWith(root.toRealPath().toString()));

            String before = get(client, "/devreload/home");
            assertTrue(before.contains("username: <span>sdelamo</span>"), before);
            notify(context, List.of(home), true);
            notify(context, List.of(home), false);
            assertEquals(before, get(client, "/devreload/home"));
        }
    }

    private Map<String, Object> devProperties() {
        return Map.of(DevelopmentMode.PROPERTY, true, "spec.name", SPEC,
            "micronaut.views.source-roots", List.of(root.toString()));
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
    @Singleton
    static class DevReloadFileSetProvider implements SoyFileSetProvider {

        private final SoyTemplateSources sources;
        private final ApplicationContext context;

        DevReloadFileSetProvider(SoyTemplateSources sources, ApplicationContext context) {
            this.sources = sources;
            this.context = context;
        }

        @Override
        public SoyFileSet provideSoyFileSet() {
            SoyFileSet.Builder builder = SoyFileSet.builder();
            if (context.getBean(ViewsSourceRoots.class).isEnabled()) {
                return sources.add(builder, "devreload.soy").build();
            }
            return sources.add(builder, "home.soy").build();
        }
    }

    @Requires(property = "spec.name", value = SPEC)
    @Controller("/devreload")
    static class DevReloadController {

        @View("devreload.page")
        @Get("/page")
        Map<String, Object> page() {
            return Map.of("name", "Sergio");
        }

        @View("sample.home")
        @Get("/home")
        Map<String, Object> home() {
            return Map.of("loggedIn", true, "username", "sdelamo");
        }
    }
}

package io.micronaut.views;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.BeanContext;
import io.micronaut.context.DefaultBeanContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.DevelopmentMode;
import io.micronaut.context.reload.ResourceKind;
import io.micronaut.context.watch.ResourceChange;
import io.micronaut.core.io.Writable;
import io.micronaut.views.exceptions.ViewNotFoundException;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ViewsReloadTest {

    private static final String SPEC = "ViewsReloadTest";

    @TempDir
    Path root;

    @Test
    void inDevelopmentModeARendererIsToldOfTheTemplatesOfItsExtensionsAndTheRootsAreTheLaunchersOnes() throws IOException {
        Path index = write("index.html", "one");
        Path notes = write("notes.txt", "notes");
        try (ApplicationContext context = ApplicationContext.run(Map.of(DevelopmentMode.PROPERTY, true, "spec.name", SPEC))) {
            assertTrue(context.containsBean(ReloadableViewsRendererRegistrar.class));
            FileRenderer renderer = context.getBean(FileRenderer.class);
            ViewsSourceRoots sourceRoots = context.getBean(ViewsSourceRoots.class);
            assertTrue(sourceRoots.isEnabled());
            assertEquals(List.of(), sourceRoots.roots());

            // the launcher reports the startup state of the views roots
            notify(context, List.of(index, notes), List.of(), true);
            assertEquals(1, renderer.changes.size());
            assertTrue(renderer.changes.get(0).initial());
            assertEquals(List.of(index), renderer.changes.get(0).changed());
            assertEquals(List.of(root), sourceRoots.roots());
            assertEquals(Optional.of(index), sourceRoots.resolve("index", "html"));
            assertEquals(Optional.of(index), sourceRoots.resolve("/index.html", ".html"));
            assertEquals(Optional.empty(), sourceRoots.resolve("missing", "html"));

            // a file of another extension does not wake the renderer, one of its own does
            notify(context, List.of(notes), List.of(), false);
            assertEquals(1, renderer.changes.size());
            notify(context, List.of(index), List.of(), false);
            assertEquals(2, renderer.changes.size());
            assertFalse(renderer.changes.get(1).initial());
        }
    }

    @Test
    void aSourceRootNeverResolvesAFileOutsideIt() throws IOException {
        Path views = Files.createDirectories(root.resolve("views"));
        Files.writeString(views.resolve("index.html"), "inside");
        Files.writeString(root.resolve("secret.html"), "outside");
        try (ApplicationContext context = ApplicationContext.run(Map.of(DevelopmentMode.PROPERTY, true, "spec.name", SPEC,
            "micronaut.views.source-roots", List.of(views.toString())))) {
            ViewsSourceRoots sourceRoots = context.getBean(ViewsSourceRoots.class);
            assertEquals(List.of(views), sourceRoots.roots());
            assertEquals(Optional.of(views.resolve("index.html")), sourceRoots.resolve("index", "html"));
            assertEquals(Optional.empty(), sourceRoots.resolve("../secret", "html"));
            // nor through a symbolic link
            Files.createSymbolicLink(views.resolve("outside"), root);
            assertEquals(Optional.empty(), sourceRoots.resolve("outside/secret", "html"));
            // a directory named with the extension is not mistaken for it
            Files.createDirectories(views.resolve("archive.html"));
            Files.writeString(views.resolve("archive.html/welcome.html"), "nested");
            assertEquals(Optional.of(views.resolve("archive.html/welcome.html")), sourceRoots.resolve("archive.html/welcome", "html"));
            assertEquals(Optional.of(views.resolve("archive.html/welcome.html")), sourceRoots.resolve("archive.html/welcome.html", "html"));
        }
    }

    @Test
    void configuredRootsComeBeforeTheLaunchersOnes() throws IOException {
        Path configured = Files.createDirectories(root.resolve("configured"));
        Path reported = Files.createDirectories(root.resolve("reported"));
        Files.writeString(configured.resolve("page.html"), "configured");
        Files.writeString(reported.resolve("page.html"), "reported");
        try (ApplicationContext context = ApplicationContext.run(Map.of(DevelopmentMode.PROPERTY, true, "spec.name", SPEC,
            "micronaut.views.source-roots", List.of(configured.toString())))) {
            ViewsSourceRoots sourceRoots = context.getBean(ViewsSourceRoots.class);
            ((DefaultBeanContext) context).notifyResourceChange(new ResourceChange(ResourceKind.VIEWS, List.of(reported), List.of(reported.resolve("page.html")), List.of(), true));
            assertEquals(List.of(configured, reported), sourceRoots.roots());
            assertEquals(Optional.of(configured.resolve("page.html")), sourceRoots.resolve("page", "html"));
        }
    }

    @Test
    void aRendererCreatedAfterTheStartupStateReceivesItAsItsFirstChange() throws IOException {
        Path index = write("index.html", "one");
        try (ApplicationContext context = ApplicationContext.run(Map.of(DevelopmentMode.PROPERTY, true, "spec.name", SPEC))) {
            notify(context, List.of(index), List.of(), true);
            FileRenderer renderer = context.getBean(FileRenderer.class);
            assertEquals(1, renderer.changes.size());
            assertTrue(renderer.changes.get(0).initial());
            assertEquals(List.of(index), renderer.changes.get(0).changed());
        }
    }

    @Test
    void inDevelopmentModeTheLocatorForgetsWhichRendererHadARemovedView() throws IOException {
        Path index = write("index.html", "one");
        try (ApplicationContext context = ApplicationContext.run(Map.of(DevelopmentMode.PROPERTY, true, "spec.name", SPEC))) {
            notify(context, List.of(index), List.of(), true);
            ViewsRendererLocator locator = context.getBean(ViewsRendererLocator.class);
            FileRenderer renderer = context.getBean(FileRenderer.class);
            assertSame(renderer, locator.resolveViewsRenderer("index", "text/html", null).orElseThrow());

            Files.delete(index);
            notify(context, List.of(), List.of(index), false);
            assertThrows(ViewNotFoundException.class, () -> locator.resolveViewsRenderer("index", "text/html", null));
        }
    }

    @Test
    void outsideDevelopmentModeNothingIsWatchedAndNoSourceRootIsRead() throws IOException {
        Path index = write("index.html", "one");
        try (ApplicationContext context = ApplicationContext.run(Map.of("spec.name", SPEC,
            "micronaut.views.source-roots", List.of(root.toString())))) {
            assertFalse(context.containsBean(ReloadableViewsRendererRegistrar.class));
            ViewsSourceRoots sourceRoots = context.getBean(ViewsSourceRoots.class);
            assertFalse(sourceRoots.isEnabled());
            assertEquals(List.of(), sourceRoots.roots());
            assertEquals(Optional.empty(), sourceRoots.resolve("index", "html"));

            FileRenderer renderer = context.getBean(FileRenderer.class);
            ViewsRendererLocator locator = context.getBean(ViewsRendererLocator.class);
            assertSame(renderer, locator.resolveViewsRenderer("index", "text/html", null).orElseThrow());

            // whatever reaches the context, the renderer is not told and the locator keeps what it found, as before
            notify(context, List.of(index), List.of(), true);
            Files.delete(index);
            notify(context, List.of(), List.of(index), false);
            assertEquals(List.of(), renderer.changes);
            assertSame(renderer, locator.resolveViewsRenderer("index", "text/html", null).orElseThrow());
        }
    }

    @Test
    void aContextThatCannotBeWatchedHasNoSourceRoots() {
        BeanContext unwatchable = (BeanContext) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{BeanContext.class}, (proxy, method, args) -> {
            throw new UnsupportedOperationException(method.getName());
        });
        assertFalse(ViewsWatches.isActive(unwatchable));
        assertEquals(null, ViewsWatches.watch(unwatchable, change -> { }));
        DefaultViewsSourceRoots sourceRoots = new DefaultViewsSourceRoots(unwatchable, new ViewsConfigurationProperties());
        assertFalse(sourceRoots.isEnabled());
        assertEquals(List.of(), sourceRoots.roots());
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

    @Singleton
    @Requires(property = "spec.name", value = SPEC)
    static class FileRenderer implements ReloadableViewsRenderer<Object, Object> {

        final List<ResourceChange> changes = new CopyOnWriteArrayList<>();
        private final ViewsSourceRoots sourceRoots;

        FileRenderer(ViewsSourceRoots sourceRoots) {
            this.sourceRoots = sourceRoots;
        }

        @Override
        public Set<String> extensions() {
            return Set.of("html");
        }

        @Override
        public void reload(ResourceChange change) {
            changes.add(change);
        }

        @Override
        public Writable render(String viewName, Object data, Object request) {
            return writer -> writer.write(Files.readString(sourceRoots.resolve(viewName, "html").orElseThrow()));
        }

        @Override
        public boolean exists(String viewName) {
            // outside development mode, the template the test wrote stands for one on the class path
            return !sourceRoots.isEnabled() || sourceRoots.resolve(viewName, "html").isPresent();
        }
    }
}

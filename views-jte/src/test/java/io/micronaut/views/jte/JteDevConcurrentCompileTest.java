package io.micronaut.views.jte;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.DefaultBeanContext;
import io.micronaut.context.env.DevelopmentMode;
import io.micronaut.context.reload.ResourceKind;
import io.micronaut.context.watch.ResourceChange;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * jte compiles a template on its first render, and again after a change, together with the templates it uses, into
 * one class directory, and loads the classes of the templates it uses lazily while it renders. Concurrent renders
 * of pages that share a layout compile that layout at the same time, and must never load its class while another
 * compilation writes it.
 */
class JteDevConcurrentCompileTest {

    private static final int PAGES = 12;
    private static final int ROUNDS = 10;

    @TempDir
    Path root;

    @TempDir
    Path classes;

    private final List<Path> files = new ArrayList<>();
    private ExecutorService executor;

    @BeforeEach
    void templates() throws IOException {
        files.add(write("race/layout.jte", layout("one")));
        files.add(write("race/header.jte", "@param String title\n<header>${title}</header>"));
        files.add(write("race/footer.jte", "<footer>footer</footer>"));
        for (int i = 0; i < PAGES; i++) {
            files.add(write("race/page" + i + ".jte", """
                @param String name
                @template.race.layout(title = "page%d", content = @`<p>%d ${name}</p>`)""".formatted(i, i)));
        }
        files.add(write("race/outer.jte", "@param Object inner\n<section>$unsafe{inner.toString()}</section>"));
        for (int i = 0; i < PAGES; i++) {
            files.add(write("race/inner" + i + ".jte", "@param int number\n<p>inner ${number}</p>"));
        }
        executor = Executors.newFixedThreadPool(PAGES);
    }

    @AfterEach
    void stop() {
        executor.shutdownNow();
    }

    @Test
    void concurrentFirstRendersOfPagesSharingALayoutAllSucceedAcrossRestarts() throws Exception {
        List<String> failures = new ArrayList<>();
        for (int restart = 0; restart < ROUNDS; restart++) {
            // every restart is a new context, whose renderer compiles into the same class directory
            try (ApplicationContext context = run()) {
                renderAll(renderer(context), "restart " + restart, "one", failures);
            }
        }
        assertEquals(List.of(), failures);
    }

    @Test
    void concurrentRendersAfterAnEditedLayoutAllSucceed() throws Exception {
        List<String> failures = new ArrayList<>();
        try (ApplicationContext context = run()) {
            HtmlJteViewsRenderer<Map<String, Object>> renderer = renderer(context);
            renderAll(renderer, "first", "one", failures);
            for (int edit = 0; edit < ROUNDS; edit++) {
                String marker = "edit" + edit;
                Path layout = write("race/layout.jte", layout(marker));
                ((DefaultBeanContext) context).notifyResourceChange(
                    new ResourceChange(ResourceKind.VIEWS, List.of(root), List.of(layout), List.of(), false));
                renderAll(renderer, marker, marker, failures);
            }
        }
        assertEquals(List.of(), failures);
    }

    @Test
    void aTemplateRenderedWithinTheRenderOfAnotherIsCompiledWhileOthersRender() throws Exception {
        try (ApplicationContext context = run()) {
            HtmlJteViewsRenderer<Map<String, Object>> renderer = renderer(context);
            CyclicBarrier start = new CyclicBarrier(PAGES);
            List<Future<String>> renders = new ArrayList<>();
            for (int i = 0; i < PAGES; i++) {
                int number = i;
                // renders a template of its own, not compiled yet, while the outer template renders
                Object inner = new Object() {
                    @Override
                    public String toString() {
                        StringWriter out = new StringWriter();
                        try {
                            renderer.render("race/inner" + number, Map.of("number", number), null).writeTo(out);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                        return out.toString();
                    }
                };
                renders.add(executor.submit(() -> {
                    start.await();
                    StringWriter out = new StringWriter();
                    renderer.render("race/outer", Map.of("inner", inner), null).writeTo(out);
                    return out.toString();
                }));
            }
            for (int i = 0; i < PAGES; i++) {
                String body = renders.get(i).get(2, TimeUnit.MINUTES);
                assertTrue(body.contains("inner " + i + "</p>"), body);
            }
        }
    }

    @Test
    void aLayoutOfTheDynamicSourceDirectoryIsCompiledAgainOnceItsFileChanges(@TempDir Path dynamic) throws Exception {
        Path layout = dynamic.resolve("dynamic/layout.jte");
        Files.createDirectories(layout.getParent());
        Files.writeString(layout, "@param gg.jte.Content content\n<header>one</header>${content}");
        Files.writeString(dynamic.resolve("dynamic/page.jte"), "@template.dynamic.layout(content = @`<p>page</p>`)");
        try (ApplicationContext context = run(Map.of("micronaut.views.jte.dynamic-source-path", dynamic.toString()))) {
            HtmlJteViewsRenderer<Map<String, Object>> renderer = renderer(context);
            assertTrue(render(renderer, "dynamic/page").contains("<header>one</header>"));

            // no change reports it: the time of the file does
            Files.writeString(layout, "@param gg.jte.Content content\n<header>two</header>${content}");
            Files.setLastModifiedTime(layout, FileTime.from(Instant.now().plusSeconds(10)));
            String body = render(renderer, "dynamic/page");
            assertTrue(body.contains("<header>two</header>") && body.contains("<p>page</p>"), body);

            // and a template added there is found
            Files.writeString(dynamic.resolve("dynamic/added.jte"), "<p>added</p>");
            assertTrue(render(renderer, "dynamic/added").contains("<p>added</p>"));
        }
    }

    private static String render(HtmlJteViewsRenderer<Map<String, Object>> renderer, String view) throws IOException {
        StringWriter out = new StringWriter();
        renderer.render(view, Map.of(), null).writeTo(out);
        return out.toString();
    }

    private ApplicationContext run() {
        return run(Map.of());
    }

    private ApplicationContext run(Map<String, Object> properties) {
        Map<String, Object> all = new HashMap<>(properties);
        all.put(DevelopmentMode.PROPERTY, true);
        all.put("micronaut.views.jte.dynamic-path", classes.toString());
        ApplicationContext context = ApplicationContext.run(all);
        ((DefaultBeanContext) context).notifyResourceChange(
            new ResourceChange(ResourceKind.VIEWS, List.of(root), files, List.of(), true));
        return context;
    }

    @SuppressWarnings("unchecked")
    private static HtmlJteViewsRenderer<Map<String, Object>> renderer(ApplicationContext context) {
        return context.getBean(HtmlJteViewsRenderer.class);
    }

    private void renderAll(HtmlJteViewsRenderer<Map<String, Object>> renderer, String round, String marker, List<String> failures) throws Exception {
        CyclicBarrier start = new CyclicBarrier(PAGES);
        List<Future<String>> renders = new ArrayList<>();
        for (int i = 0; i < PAGES; i++) {
            String view = "race/page" + i;
            renders.add(executor.submit(() -> {
                start.await();
                StringWriter out = new StringWriter();
                renderer.render(view, Map.of("name", "Sergio"), null).writeTo(out);
                return out.toString();
            }));
        }
        for (int i = 0; i < PAGES; i++) {
            try {
                String body = renders.get(i).get(2, TimeUnit.MINUTES);
                assertTrue(body.contains("<title>page" + i + " " + marker + "</title>") && body.contains("<p>" + i + " Sergio</p>")
                    && body.contains("<header>page" + i + "</header>") && body.contains("<footer>footer</footer>"), body);
            } catch (ExecutionException e) {
                failures.add(round + ", page" + i + ": " + rootCause(e));
            }
        }
    }

    private static String layout(String marker) {
        return """
            @param gg.jte.Content content
            @param String title
            <html><head><title>${title} %s</title></head><body>@template.race.header(title = title)${content}@template.race.footer()</body></html>""".formatted(marker);
    }

    private static String rootCause(Throwable e) {
        Throwable cause = e;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause.getClass().getName() + ": " + cause.getMessage();
    }

    private Path write(String name, String content) throws IOException {
        Path file = root.resolve(name);
        Files.createDirectories(file.getParent());
        Files.writeString(file, content);
        return file;
    }
}

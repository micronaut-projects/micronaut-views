package io.micronaut.views.rocker;

import com.fizzed.rocker.RockerModel;
import io.micronaut.views.ViewsSourceRoots;
import io.micronaut.views.exceptions.ViewRenderingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RockerSourceCompilerTest {

    private static final String PAGE = "views/page.rocker.html";

    @TempDir
    Path root;

    @Test
    void repeatedTemplateEditsDeleteTheCompilationsNoModelUsesAndKeepTheOneInUse() throws IOException, InterruptedException {
        Path page = root.resolve("page.rocker.html");
        Files.writeString(page, "<p>edit 0</p>\n");
        RockerSourceCompiler compiler = new RockerSourceCompiler("views/", sourceRoots(root), getClass().getClassLoader());
        Path workDirectory;
        try {
            // a model of the first compilation is still in use, as one rendering while the template is edited
            RockerModel inUse = compiler.model(PAGE).orElseThrow();
            workDirectory = compiler.workDirectory();
            assertNotNull(workDirectory);
            Path firstCompilation = workDirectory.resolve("1");

            int edit = 1;
            for (; edit <= 5; edit++) {
                assertEquals("<p>edit " + edit + "</p>", render(compiler, page, edit));
            }
            // a compilation that fails loads nothing from its directory
            Files.writeString(page, "@args (String name)\n<p>@name.noSuchMethod()</p>\n");
            compiler.invalidate();
            assertThrows(ViewRenderingException.class, () -> compiler.model(PAGE));
            Path failedCompilation = workDirectory.resolve(Integer.toString(edit + 1));
            assertTrue(Files.isDirectory(failedCompilation));

            // the model in use, the compilation just replaced, which a collection has yet to find, and the current one
            List<String> compilations = List.of();
            for (int attempt = 0; attempt < 50; attempt++) {
                System.gc();
                Thread.sleep(20);
                assertEquals("<p>edit " + edit + "</p>", render(compiler, page, edit));
                edit++;
                compilations = compilations(workDirectory);
                if (compilations.size() <= 3) {
                    break;
                }
            }
            assertTrue(compilations.size() <= 3, "the retired compilations are deleted: " + compilations);
            assertFalse(Files.exists(failedCompilation), "the failed compilation is deleted once replaced");

            // with no further edit, a lookup deletes the compilation just replaced once a collection finds it
            for (int attempt = 0; attempt < 50 && compilations.size() > 2; attempt++) {
                System.gc();
                Thread.sleep(20);
                compiler.model(PAGE).orElseThrow();
                compilations = compilations(workDirectory);
            }
            assertEquals(2, compilations.size(), "an idle session reclaims the retired compilations: " + compilations);
            assertTrue(Files.isDirectory(firstCompilation), "the compilation of a model in use is kept: " + compilations);
            assertEquals("<p>edit 0</p>", inUse.render().toString().trim());
        } finally {
            compiler.close();
        }
        assertFalse(Files.exists(workDirectory), "closing deletes every compilation");
    }

    private static String render(RockerSourceCompiler compiler, Path page, int edit) throws IOException {
        Files.writeString(page, "<p>edit " + edit + "</p>\n");
        compiler.invalidate();
        return compiler.model(PAGE).orElseThrow().render().toString().trim();
    }

    private static List<String> compilations(Path workDirectory) throws IOException {
        try (Stream<Path> files = Files.list(workDirectory)) {
            return files.map(file -> file.getFileName().toString()).sorted().toList();
        }
    }

    private static ViewsSourceRoots sourceRoots(Path root) {
        return new ViewsSourceRoots() {
            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public List<Path> roots() {
                return List.of(root);
            }

            @Override
            public Optional<Path> resolve(String viewName, String extension) {
                Path file = root.resolve(viewName);
                return Files.isRegularFile(file) ? Optional.of(file) : Optional.empty();
            }
        };
    }
}

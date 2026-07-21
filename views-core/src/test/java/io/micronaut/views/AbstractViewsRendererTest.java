/*
 * Copyright 2017-2026 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.views;

import io.micronaut.core.io.ResourceLoader;
import io.micronaut.core.io.Writable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractViewsRendererTest {

    @Test
    void viewHelpersSupportConfiguredExtensionsWithAndWithoutLeadingDot() {
        assertViewHelpers("html");
        assertViewHelpers(".html");
    }

    @Test
    void existsUsesTheConfiguredFolderAndExtension() {
        TestViewsRenderer renderer = new TestViewsRenderer("html", new TestResourceLoader(Set.of("views/home.html")));

        assertTrue(renderer.exists("home"));
        assertFalse(renderer.exists("missing"));
    }

    @Test
    void viewHelpersFailWhenTheDefaultExtensionIsNull() {
        TestViewsRenderer renderer = new TestViewsRenderer(null, new TestResourceLoader(Set.of()));

        assertThrows(NullPointerException.class, () -> renderer.viewNameWithExtension("home"));
        assertThrows(NullPointerException.class, () -> renderer.viewLocationWithoutExtension("home"));
        assertThrows(NullPointerException.class, () -> renderer.viewLocationWithExtension("home"));
        assertThrows(NullPointerException.class, () -> renderer.exists("home"));
    }

    private static void assertViewHelpers(String extension) {
        TestViewsRenderer renderer = new TestViewsRenderer(extension, new TestResourceLoader(Set.of()));

        assertEquals("home.html", renderer.viewNameWithExtension("home"));
        assertEquals("home.html", renderer.viewNameWithExtension("home.html"));
        assertEquals("views/home", renderer.viewLocationWithoutExtension("home"));
        assertEquals("views/home", renderer.viewLocationWithoutExtension("home.html"));
        assertEquals("views/home.html", renderer.viewLocationWithExtension("home"));
        assertEquals("views/home.html", renderer.viewLocationWithExtension("home.html"));
    }

    private static final class TestViewsRenderer extends AbstractViewsRenderer<Object, Object> {

        private TestViewsRenderer(@Nullable String extension, @NonNull ResourceLoader resourceLoader) {
            super(new TestViewsRendererConfiguration(extension), "views/", resourceLoader);
        }

        @Override
        public @NonNull Writable render(@NonNull String viewName, @Nullable Object data, @Nullable Object request) {
            throw new UnsupportedOperationException();
        }
    }

    private record TestViewsRendererConfiguration(@Nullable String extension) implements ViewsRendererConfiguration {

        @Override
        public @Nullable String getDefaultExtension() {
            return extension;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    private record TestResourceLoader(Set<String> resourcePaths) implements ResourceLoader {

        @Override
        public Optional<InputStream> getResourceAsStream(String path) {
            return Optional.empty();
        }

        @Override
        public Optional<URL> getResource(String path) {
            return resourcePaths.contains(path) ? Optional.of(resourceUrl()) : Optional.empty();
        }

        @Override
        public Stream<URL> getResources(String name) {
            return Stream.empty();
        }

        @Override
        public boolean supportsPrefix(String path) {
            return false;
        }

        @Override
        public ResourceLoader forBase(String basePath) {
            return this;
        }

        private static URL resourceUrl() {
            try {
                return new URL("file:/template");
            } catch (MalformedURLException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}

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
package io.micronaut.views.jte;

import gg.jte.CodeResolver;
import gg.jte.resolve.DirectoryCodeResolver;
import io.micronaut.context.watch.ResourceChange;
import io.micronaut.core.annotation.Internal;
import io.micronaut.views.ViewsSourceRoots;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The code resolver of development mode: a template under a {@link ViewsSourceRoots views source root} is read
 * from there, then from a deprecated dynamic source directory, then from the class path.
 *
 * <p>A template of the source roots is compiled again only once a change reports it: its last modification is a
 * stamp that {@link #changed(ResourceChange)} moves on, not the time of the file. On every render, jte compares the
 * stamps of a template and of the templates it uses with those it compiled them with, so an edited layout
 * recompiles the pages that use it.</p>
 *
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
final class SourceRootsCodeResolver implements CodeResolver {

    private final ViewsSourceRoots sourceRoots;
    private final @Nullable DirectoryCodeResolver dynamicSource;
    private final CodeResolver classPath;
    private final Map<String, Long> stamps = new ConcurrentHashMap<>();
    // jte recompiles a template when a stamp is later than the one it compiled with, so the stamps of changes
    // start after any time of a file, which the dynamic source directory still reports
    private final AtomicLong stamp = new AtomicLong(1L << 62);

    /**
     * @param sourceRoots The views source roots
     * @param dynamicSource The source directory of the deprecated dynamic mode, read after the source roots
     * @param classPath The resolver of the class path
     */
    SourceRootsCodeResolver(ViewsSourceRoots sourceRoots, @Nullable Path dynamicSource, CodeResolver classPath) {
        this.sourceRoots = sourceRoots;
        this.dynamicSource = dynamicSource == null ? null : new DirectoryCodeResolver(dynamicSource);
        this.classPath = classPath;
    }

    /**
     * @param name The template name, such as {@code home.jte}
     * @return Whether the template is read from a source directory rather than the class path
     */
    boolean isSource(String name) {
        return sourceRoots.resolve(name, null).isPresent() || (dynamicSource != null && dynamicSource.exists(name));
    }

    /**
     * Moves the stamp of every template a change reports, so that jte compiles it, and the templates that use
     * it, again on their next render.
     *
     * @param change The change
     */
    void changed(ResourceChange change) {
        List<Path> paths = new ArrayList<>(change.changed().size() + change.removed().size());
        paths.addAll(change.changed());
        paths.addAll(change.removed());
        // a configured source root may lie below a watched root: the name is relative to every root holding the file
        Set<Path> roots = new LinkedHashSet<>();
        for (Path root : sourceRoots.roots()) {
            roots.add(root.toAbsolutePath().normalize());
        }
        for (Path root : change.roots()) {
            roots.add(root.toAbsolutePath().normalize());
        }
        for (Path path : paths) {
            Path file = path.toAbsolutePath().normalize();
            long next = stamp.incrementAndGet();
            for (Path root : roots) {
                if (file.startsWith(root) && !file.equals(root)) {
                    stamps.put(root.relativize(file).toString().replace('\\', '/'), next);
                }
            }
        }
    }

    @Override
    public @Nullable String resolve(String name) {
        Optional<Path> source = sourceRoots.resolve(name, null);
        if (source.isPresent()) {
            try {
                return Files.readString(source.get(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new UncheckedIOException("Could not read template " + source.get(), e);
            }
        }
        if (dynamicSource != null && dynamicSource.exists(name)) {
            return dynamicSource.resolve(name);
        }
        return classPath.resolve(name);
    }

    @Override
    public long getLastModified(String name) {
        if (sourceRoots.resolve(name, null).isPresent()) {
            return stamps.getOrDefault(name, 0L);
        }
        if (dynamicSource != null && dynamicSource.exists(name)) {
            // not watched: jte follows the time of the file, as the dynamic mode did, or a template that went from
            // the source roots
            return Math.max(stamps.getOrDefault(name, 0L), dynamicSource.getLastModified(name));
        }
        // a template that went from the source roots is compiled again from the class path
        return stamps.getOrDefault(name, 0L);
    }

    @Override
    public boolean exists(String name) {
        return isSource(name) || classPath.exists(name);
    }
}

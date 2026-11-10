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

import io.micronaut.context.BeanContext;
import io.micronaut.context.watch.ResourceChange;
import io.micronaut.core.annotation.Internal;
import jakarta.inject.Singleton;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * The default {@link ViewsSourceRoots}: in development mode, the roots configured with
 * {@code micronaut.views.source-roots}, then the roots of the {@code views} resource kind the
 * development launcher reports through the context's resource watch; outside development mode, none.
 *
 * @author graemerocher
 */
@Internal
@Singleton
final class DefaultViewsSourceRoots implements ViewsSourceRoots {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultViewsSourceRoots.class);

    private final boolean enabled;
    private final List<Path> configured;
    private volatile List<Path> reported = List.of();

    /**
     * @param beanContext The context
     * @param viewsConfiguration The views configuration
     */
    DefaultViewsSourceRoots(BeanContext beanContext, ViewsConfiguration viewsConfiguration) {
        this.enabled = ViewsWatches.isActive(beanContext);
        if (enabled) {
            List<Path> paths = new ArrayList<>();
            for (String root : viewsConfiguration.getSourceRoots()) {
                paths.add(Path.of(root).toAbsolutePath().normalize());
            }
            this.configured = List.copyOf(paths);
            // the launcher reports the roots of the views kind with every batch, the startup state first
            ViewsWatches.watch(beanContext, this::rootsReported);
        } else {
            this.configured = List.of();
        }
    }

    private void rootsReported(ResourceChange change) {
        List<Path> roots = new ArrayList<>(change.roots().size());
        for (Path root : change.roots()) {
            roots.add(root.toAbsolutePath().normalize());
        }
        reported = List.copyOf(roots);
        if (change.initial()) {
            // only the launcher's roots are watched: an edit under a configured root outside them reaches no engine
            for (Path root : configured) {
                if (roots.stream().noneMatch(root::startsWith)) {
                    LOG.warn("The views source root {} is not under a views root the development launcher watches ({}): templates are read from it, but an edit there does not clear the engines' caches. Add it to micronaut.dev.resources.views.", root, roots);
                }
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public @NonNull List<Path> roots() {
        if (!enabled) {
            return List.of();
        }
        List<Path> reportedRoots = reported;
        if (reportedRoots.isEmpty()) {
            return configured;
        }
        if (configured.isEmpty()) {
            return reportedRoots;
        }
        Set<Path> roots = new LinkedHashSet<>(configured);
        roots.addAll(reportedRoots);
        return List.copyOf(roots);
    }

    @Override
    public @NonNull Optional<Path> resolve(@NonNull String viewName, @Nullable String extension) {
        if (!enabled) {
            return Optional.empty();
        }
        List<Path> roots = roots();
        if (roots.isEmpty()) {
            return Optional.empty();
        }
        String name = viewName.replace('\\', '/');
        while (name.startsWith("/")) {
            name = name.substring(1);
        }
        if (extension != null && !extension.isEmpty()) {
            String suffix = extension.startsWith(ViewUtils.EXTENSION_SEPARATOR) ? extension : ViewUtils.EXTENSION_SEPARATOR + extension;
            // appended unless the whole name already ends with it; a directory such as archive.html/ is left as it is
            if (!name.endsWith(suffix)) {
                name = name + suffix;
            }
        }
        if (name.isEmpty()) {
            return Optional.empty();
        }
        for (Path root : roots) {
            Path candidate;
            try {
                candidate = root.resolve(name).normalize();
            } catch (InvalidPathException e) {
                return Optional.empty();
            }
            // a name that climbs out of the root resolves to nothing, as it would on the class path, and so does
            // one that leaves it through a symbolic link
            if (candidate.startsWith(root) && Files.isRegularFile(candidate) && isWithin(root, candidate)) {
                return Optional.of(candidate);
            }
        }
        return Optional.empty();
    }

    private static boolean isWithin(Path root, Path candidate) {
        try {
            return candidate.toRealPath().startsWith(root.toRealPath());
        } catch (IOException e) {
            return false;
        }
    }
}

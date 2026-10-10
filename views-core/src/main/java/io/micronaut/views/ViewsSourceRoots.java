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

import io.micronaut.core.annotation.Experimental;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Where view sources live in development mode, so that a template engine reads an edited template from
 * the project's sources, ahead of the class path, without waiting for the build to copy it.
 *
 * <p>A root is the source directory of the views folder ({@link ViewsConfiguration#getFolder()}), such as
 * {@code src/main/resources/views}: a view named {@code home} with the extension {@code html} resolves
 * to {@code <root>/home.html}. The roots are those of the {@code views} resource kind that a development
 * launcher such as {@code micronaut-dev} reports to the context (its manifest's
 * {@code micronaut.dev.resources.views}), plus any configured with {@code micronaut.views.source-roots}.</p>
 *
 * <p>Outside development mode there are no roots, and engines resolve views from the class path as they
 * always did.</p>
 *
 * @author graemerocher
 * @since 6.4.0
 */
@Experimental
public interface ViewsSourceRoots {

    /**
     * @return Whether views may be resolved from source roots at all, which is the case in development mode only
     */
    boolean isEnabled();

    /**
     * @return The source roots, in the order they are searched; empty outside development mode
     */
    @NonNull
    List<Path> roots();

    /**
     * Resolves a view to the source file that holds it.
     *
     * @param viewName The view name, relative to the views folder, such as {@code home} or {@code mail/welcome.html}
     * @param extension The extension, with or without the dot, appended to the name unless it already ends with
     *                  it; or null to use the name as it is
     * @return The first regular file under a root, never one outside the roots
     */
    @NonNull
    Optional<Path> resolve(@NonNull String viewName, @Nullable String extension);

    /**
     * @return Source roots that resolve nothing, as outside development mode
     */
    @NonNull
    static ViewsSourceRoots none() {
        return NoViewsSourceRoots.INSTANCE;
    }

}

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

import io.micronaut.context.watch.ResourceChange;
import io.micronaut.core.annotation.Experimental;
import org.jspecify.annotations.NonNull;

import java.util.Set;

/**
 * A renderer that caches what it compiled from templates, and can drop what an edit of them made stale.
 *
 * <p>In development mode views-core registers one resource watch per extension the renderer
 * {@link #extensions() renders}, on the {@code views} resource kind, through
 * {@link io.micronaut.context.WatchableBeanContext#watchResources}. The renderer's {@link #reload(ResourceChange)}
 * receives the templates of its extensions that changed or went, after the launcher made the new contents
 * readable. Outside development mode nothing is registered and {@link #reload(ResourceChange)} is never called.</p>
 *
 * <p>The watches belong to the renderer bean and close with it.</p>
 *
 * @param <T> The model type
 * @param <R> The request type
 * @author graemerocher
 * @since 6.4.0
 */
@Experimental
public interface ReloadableViewsRenderer<T, R> extends ViewsRenderer<T, R> {

    /**
     * The extensions of the templates this renderer reads, without the dot, such as {@code html} for
     * Thymeleaf. An empty set watches every file of the views roots.
     *
     * @return The extensions
     */
    @NonNull
    Set<String> extensions();

    /**
     * Drops or recompiles what the change made stale. The first change a watch delivers is the
     * {@link ResourceChange#initial() initial} state, which lists the templates present.
     *
     * @param change The templates of the renderer's extensions that changed or went
     */
    void reload(@NonNull ResourceChange change);
}

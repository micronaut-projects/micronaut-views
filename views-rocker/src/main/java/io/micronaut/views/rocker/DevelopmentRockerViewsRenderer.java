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
package io.micronaut.views.rocker;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.env.DevelopmentActive;
import io.micronaut.context.watch.ResourceChange;
import io.micronaut.core.annotation.Internal;
import io.micronaut.views.ReloadableViewsRenderer;
import io.micronaut.views.ViewsConfiguration;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.Set;

/**
 * The Rocker renderer of development mode: a change of a Rocker template compiles the templates of the views source
 * roots again on the next render, through the {@link DevelopmentRockerEngine}.
 *
 * @param <T> The model type
 * @param <R> The request type
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
@Singleton
@DevelopmentActive
@Replaces(RockerViewsRenderer.class)
final class DevelopmentRockerViewsRenderer<T, R> extends RockerViewsRenderer<T, R> implements ReloadableViewsRenderer<T, R> {

    /**
     * @param viewsConfiguration  Views Configuration
     * @param rockerConfiguration Rocker Configuration
     * @param rockerEngine        Rocker Engine
     */
    @Inject
    DevelopmentRockerViewsRenderer(ViewsConfiguration viewsConfiguration,
                                   RockerViewsRendererConfiguration rockerConfiguration,
                                   RockerEngine rockerEngine) {
        super(viewsConfiguration, rockerConfiguration, rockerEngine);
    }

    /**
     * None, to watch every views file: a template may call one of another content type, such as
     * {@code .rocker.raw}. Only a change of a Rocker template compiles the templates again.
     *
     * @return No extension
     */
    @Override
    public @NonNull Set<String> extensions() {
        return Set.of();
    }

    /**
     * Compiles the Rocker templates of the views source roots again on the next render, when a Rocker template
     * changed or went. With no runtime compiler, nothing is compiled: the templates are classes, which follow the
     * class reload.
     *
     * @param change The views files that changed or went
     */
    @Override
    public void reload(@NonNull ResourceChange change) {
        if (rockerEngine instanceof DevelopmentRockerEngine engine
            && (change.initial() || change.changed().stream().anyMatch(DevelopmentRockerViewsRenderer::isRockerTemplate)
                || change.removed().stream().anyMatch(DevelopmentRockerViewsRenderer::isRockerTemplate))) {
            engine.invalidate();
        }
    }

    private static boolean isRockerTemplate(Path file) {
        Path name = file.getFileName();
        return name != null && name.toString().contains(".rocker.");
    }
}

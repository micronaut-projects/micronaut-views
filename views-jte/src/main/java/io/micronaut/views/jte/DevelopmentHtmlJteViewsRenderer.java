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

import gg.jte.ContentType;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.DevelopmentActive;
import io.micronaut.context.watch.ResourceChange;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.io.Writable;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import io.micronaut.views.ReloadableViewsRenderer;
import io.micronaut.views.ViewUtils;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsSourceRoots;
import jakarta.inject.Singleton;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.nio.file.Paths;
import java.util.Set;

/**
 * The text/html JTE renderer of development mode: templates of the views source roots are compiled at runtime, and
 * compiled again after a change, through {@link DevelopmentJteTemplates}. The production
 * {@link HtmlJteViewsRenderer} renders precompiled templates, or dynamic ones when so configured.
 *
 * @param <T> type of input model.
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
@Produces(value = {MediaType.TEXT_HTML})
@Primary
@Singleton
@Requires(classes = HttpRequest.class)
@DevelopmentActive
@Replaces(HtmlJteViewsRenderer.class)
final class DevelopmentHtmlJteViewsRenderer<T> extends HtmlJteViewsRenderer<T> implements ReloadableViewsRenderer<T, HttpRequest<?>> {

    private final @Nullable DevelopmentJteTemplates templates;

    /**
     * @param viewsConfiguration Views Configuration
     * @param jteViewsRendererConfiguration JTE specific configuration
     * @param sourceRoots The views source roots, whose templates are compiled at runtime
     * @param resourceLoader The class path resource loader, whose class loader templates compiled at runtime use
     */
    DevelopmentHtmlJteViewsRenderer(ViewsConfiguration viewsConfiguration,
                                JteViewsRendererConfiguration jteViewsRendererConfiguration,
                                ViewsSourceRoots sourceRoots,
                                @Nullable ClassPathResourceLoader resourceLoader) {
        super(viewsConfiguration, jteViewsRendererConfiguration);
        this.templates = sourceRoots.isEnabled()
            ? new DevelopmentJteTemplates(viewsConfiguration, jteViewsRendererConfiguration, ContentType.Html,
                Paths.get(jteViewsRendererConfiguration.getDynamicPath()).resolve("html"), sourceRoots,
                resourceLoader != null ? resourceLoader.getClassLoader() : null)
            : null;
    }

    @Override
    public @NonNull Writable render(@NonNull String viewName, @Nullable T data, @Nullable HttpRequest<?> request) {
        if (templates == null) {
            return super.render(viewName, data, request);
        }
        return templates.render(viewName, ViewUtils.modelOf(data), this::decorateOutput);
    }

    @Override
    public boolean exists(@NonNull String viewName) {
        return templates == null ? super.exists(viewName) : templates.exists(viewName);
    }

    /**
     * The extensions of jte templates, {@code jte} and {@code kte}.
     *
     * @return The extensions
     */
    @Override
    public @NonNull Set<String> extensions() {
        return DevelopmentJteTemplates.WATCHED_EXTENSIONS;
    }

    /**
     * Has the templates the change reports, and those that use them, compiled again on their next render.
     * Precompiled templates are classes, and follow the class reload instead.
     *
     * @param change The templates that changed or went
     */
    @Override
    public void reload(@NonNull ResourceChange change) {
        if (templates != null) {
            templates.changed(change);
        }
    }
}

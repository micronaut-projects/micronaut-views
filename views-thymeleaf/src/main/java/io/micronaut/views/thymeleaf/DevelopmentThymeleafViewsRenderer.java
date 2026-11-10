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
package io.micronaut.views.thymeleaf;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.DevelopmentActive;
import io.micronaut.context.watch.ResourceChange;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.server.util.locale.HttpLocaleResolver;
import io.micronaut.views.ReloadableViewsRenderer;
import io.micronaut.views.ViewsSourceRoots;
import jakarta.inject.Singleton;
import org.jspecify.annotations.NonNull;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;

import java.util.Set;

/**
 * The Thymeleaf renderer of development mode: a template of the views source roots exists too, and an edit of the
 * views clears the engine's template cache. The production {@link ThymeleafViewsRenderer} looks templates up on the
 * class path only.
 *
 * @param <T> The model type
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
@Singleton
@DevelopmentActive
@Requires(classes = {HttpRequest.class, HttpLocaleResolver.class})
@Replaces(ThymeleafViewsRenderer.class)
final class DevelopmentThymeleafViewsRenderer<T> extends ThymeleafViewsRenderer<T> implements ReloadableViewsRenderer<T, HttpRequest<?>> {

    private final ViewsSourceRoots sourceRoots;

    /**
     * @param templateResolver   The template resolver
     * @param templateEngine     The template engine
     * @param resourceLoader     The resource loader
     * @param httpLocaleResolver The locale resolver
     * @param sourceRoots        The views source roots
     */
    DevelopmentThymeleafViewsRenderer(AbstractConfigurableTemplateResolver templateResolver,
                                      TemplateEngine templateEngine,
                                      ClassPathResourceLoader resourceLoader,
                                      HttpLocaleResolver httpLocaleResolver,
                                      ViewsSourceRoots sourceRoots) {
        super(templateResolver, templateEngine, resourceLoader, httpLocaleResolver);
        this.sourceRoots = sourceRoots;
    }

    @Override
    public boolean exists(@NonNull String viewName) {
        if (sourceRoots.isEnabled() && existsInSourceRoots(resolveTemplate(viewName).templateName())) {
            return true;
        }
        return super.exists(viewName);
    }

    /**
     * The extension of the configured suffix when the resolver forces it. Otherwise a template named with an
     * extension of its own, such as {@code layout.xml}, is read as it is named, so every views file is watched.
     *
     * @return The extensions, or none to watch every views file
     */
    @Override
    public @NonNull Set<String> extensions() {
        String suffix = templateResolver.getSuffix();
        if (suffix == null || !templateResolver.getForceSuffix()) {
            return Set.of();
        }
        int dot = suffix.lastIndexOf('.');
        String extension = dot < 0 ? suffix : suffix.substring(dot + 1);
        return extension.isEmpty() ? Set.of() : Set.of(extension);
    }

    /**
     * Clears the template cache of the engine: a template may be a fragment or a layout of others.
     *
     * @param change The templates that changed or went
     */
    @Override
    public void reload(@NonNull ResourceChange change) {
        engine.clearTemplateCache();
    }

    private boolean existsInSourceRoots(String templateName) {
        // as the resolver names it: a template named with an extension of its own is read as it is named unless the
        // suffix is forced
        String file = templateName.substring(templateName.lastIndexOf('/') + 1);
        if (!templateResolver.getForceSuffix() && file.indexOf('.') > 0 && sourceRoots.resolve(templateName, null).isPresent()) {
            return true;
        }
        return sourceRoots.resolve(templateName, templateResolver.getSuffix()).isPresent();
    }
}

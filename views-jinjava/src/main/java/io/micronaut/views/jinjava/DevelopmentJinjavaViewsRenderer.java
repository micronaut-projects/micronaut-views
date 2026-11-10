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
package io.micronaut.views.jinjava;

import com.hubspot.jinjava.Jinjava;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.DevelopmentActive;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.io.Writable;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.core.util.StringUtils;
import io.micronaut.views.ViewUtils;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsRendererConfiguration;
import io.micronaut.views.ViewsSourceRoots;
import io.micronaut.views.exceptions.ViewRenderingException;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * The Jinjava renderer of development mode: a template of the views source roots is read from there, so that an
 * edit is rendered without a copy step. The production {@link JinjavaViewsRenderer} reads the class path only.
 *
 * @param <T> The model type
 * @param <R> The request type
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
@Singleton
@DevelopmentActive
@Requires(property = JinjavaViewsRendererConfigurationProperties.PREFIX + ".enabled", notEquals = StringUtils.FALSE)
@Requires(classes = Jinjava.class)
@Replaces(JinjavaViewsRenderer.class)
final class DevelopmentJinjavaViewsRenderer<T, R> extends JinjavaViewsRenderer<T, R> {

    private final Jinjava jinjava;
    private final ViewsSourceRoots sourceRoots;

    /**
     * @param viewsRendererConfiguration Jinjava Views configuration
     * @param viewsConfiguration Views Configuration
     * @param resourceLoader Resources Loader
     * @param jinjava The configured Jinjava engine
     * @param sourceRoots The views source roots
     */
    DevelopmentJinjavaViewsRenderer(@Named("jinjava") ViewsRendererConfiguration viewsRendererConfiguration,
                                    ViewsConfiguration viewsConfiguration,
                                    ClassPathResourceLoader resourceLoader,
                                    Jinjava jinjava,
                                    ViewsSourceRoots sourceRoots) {
        super(viewsRendererConfiguration, viewsConfiguration, resourceLoader, jinjava);
        this.jinjava = jinjava;
        this.sourceRoots = sourceRoots;
    }

    @Override
    public Writable render(String viewName, @Nullable T data, @Nullable R request) {
        Optional<String> source = readSource(viewName);
        if (source.isEmpty()) {
            return super.render(viewName, data, request);
        }
        String template = source.get();
        return writer -> {
            try {
                writer.write(jinjava.render(template, ViewUtils.modelOf(data)));
            } catch (RuntimeException e) {
                throw new ViewRenderingException("Error rendering Jinjava view [" + viewName + "]: " + e.getMessage(), e);
            }
        };
    }

    @Override
    public boolean exists(String viewName) {
        return super.exists(viewName) || sourceRoots.resolve(viewNameWithExtension(viewName), null).isPresent();
    }

    private Optional<String> readSource(String viewName) {
        Optional<Path> source = sourceRoots.resolve(viewNameWithExtension(viewName), null);
        if (source.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Files.readString(source.get(), jinjava.getGlobalConfig().getCharset()));
        } catch (IOException e) {
            throw new ViewRenderingException("Error rendering Jinjava view [" + viewName + "]: " + e.getMessage(), e);
        }
    }
}

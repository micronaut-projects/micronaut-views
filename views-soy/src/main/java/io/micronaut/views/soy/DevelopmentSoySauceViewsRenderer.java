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
package io.micronaut.views.soy;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.error.SoyCompilationException;
import com.google.template.soy.jbcsrc.api.SoySauce;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.DevelopmentActive;
import io.micronaut.context.watch.ResourceChange;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.io.Writable;
import io.micronaut.http.HttpRequest;
import io.micronaut.views.ReloadableViewsRenderer;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.csp.CspConfiguration;
import io.micronaut.views.exceptions.ViewRenderingException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * The Soy renderer of development mode: it compiles the file set again when a Soy file changes, and renders the
 * latest compilation from then on. The production {@link SoySauceViewsRenderer} renders the templates it compiled
 * when it was created.
 *
 * @param <T> The model type
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
@Requires(classes = SoySauce.class)
@Requires(classes = HttpRequest.class)
@DevelopmentActive
@Singleton
@Replaces(SoySauceViewsRenderer.class)
final class DevelopmentSoySauceViewsRenderer<T> extends SoySauceViewsRenderer<T>
    implements ReloadableViewsRenderer<T, HttpRequest<?>> {

    private static final Logger LOG = LoggerFactory.getLogger(DevelopmentSoySauceViewsRenderer.class);

    private final boolean precompiled;
    private volatile SoySauce current;
    private volatile @Nullable ViewRenderingException compileFailure;

    /**
     * @param viewsConfiguration Views configuration properties.
     * @param cspConfiguration Content-Security-Policy configuration.
     * @param namingMapProvider Soy naming map provider
     * @param soyConfiguration   Soy configuration properties.
     */
    @Inject
    DevelopmentSoySauceViewsRenderer(ViewsConfiguration viewsConfiguration,
                                     @Nullable CspConfiguration cspConfiguration,
                                     @Nullable SoyNamingMapProvider namingMapProvider,
                                     SoyViewsRendererConfigurationProperties soyConfiguration) {
        // the provider is asked once, so that what is reloaded follows the templates the renderer started with
        this(viewsConfiguration, cspConfiguration, namingMapProvider, soyConfiguration, soyConfiguration.getCompiledTemplates());
    }

    private DevelopmentSoySauceViewsRenderer(ViewsConfiguration viewsConfiguration,
                                             @Nullable CspConfiguration cspConfiguration,
                                             @Nullable SoyNamingMapProvider namingMapProvider,
                                             SoyViewsRendererConfigurationProperties soyConfiguration,
                                             @Nullable SoySauce compiledTemplates) {
        super(viewsConfiguration, cspConfiguration, namingMapProvider, soyConfiguration, compiledTemplates);
        this.precompiled = compiledTemplates != null;
        this.current = soySauce;
    }

    /**
     * The Soy files: a change to one compiles the file set again.
     *
     * @return {@code soy}
     */
    @Override
    public @NonNull Set<String> extensions() {
        return Set.of("soy");
    }

    /**
     * Compiles the file set again, as the {@link SoyFileSetProvider} provides it, and renders the new templates
     * from then on. A provider whose file set reads its files from URLs or files, such as one built with
     * {@link SoyTemplateSources}, reads the edited files. While the file set does not compile, rendering fails
     * with the compilation error, until a change compiles it again. Templates the provider precompiled are left
     * as they are: they are classes, and follow the class reload.
     *
     * @param change The Soy files that changed or went
     */
    @Override
    public void reload(@NonNull ResourceChange change) {
        if (precompiled) {
            return;
        }
        try {
            current = compile();
            compileFailure = null;
        } catch (ViewRenderingException e) {
            LOG.error("{}", e.getMessage());
            compileFailure = e;
        } catch (RuntimeException e) {
            // the provider failed to build the file set, for example because a file it adds was removed
            LOG.error("Soy file set could not be built: {}", e.getMessage());
            compileFailure = new ViewRenderingException("Soy file set could not be built: " + e.getMessage(), e);
        }
    }

    @Override
    public @NonNull Writable render(@NonNull String viewName, @Nullable T data, @Nullable HttpRequest<?> request) {
        ViewRenderingException failure = compileFailure;
        if (failure != null) {
            throw failure;
        }
        return render(current, viewName, data, request);
    }

    @Override
    public boolean exists(@NonNull String view) {
        return current.hasTemplate(view);
    }

    private SoySauce compile() {
        SoyFileSet fileSet = soyMicronautConfiguration.getFileSet();
        if (fileSet == null) {
            throw new IllegalStateException(
                "Unable to load Soy templates: no file set, no compiled templates provided.");
        }
        try {
            return fileSet.compileTemplates();
        } catch (SoyCompilationException se) {
            throw new ViewRenderingException(
                "Soy template compilation failed: " + se.getMessage(), se);
        }
    }
}

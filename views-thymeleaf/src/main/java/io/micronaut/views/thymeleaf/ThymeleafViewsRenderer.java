/*
 * Copyright 2017-2020 original authors
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

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.io.ResourceLoader;
import io.micronaut.core.io.Writable;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.core.util.ArgumentUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.views.ViewUtils;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.WritableViewsRenderer;
import io.micronaut.views.exceptions.ViewRenderingException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.exceptions.TemplateEngineException;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.Writer;
import java.util.Locale;

/**
 * Renders templates Thymeleaf Java template engine.
 *
 * @author Sergio del Amo
 * @author graemerocher
 *
 * @see <a href="https://www.thymeleaf.org">https://www.thymeleaf.org</a>
 * @since 1.0
 * @param <T> The model type
 */
@Singleton
public class ThymeleafViewsRenderer<T> implements WritableViewsRenderer<T> {

    protected final AbstractConfigurableTemplateResolver templateResolver;
    protected final TemplateEngine engine;
    protected ResourceLoader resourceLoader;

    /**
     * @param templateResolver   The template resolver
     * @param templateEngine     The template engine
     * @param resourceLoader     The resource loader
     */
    @Inject
    public ThymeleafViewsRenderer(AbstractConfigurableTemplateResolver templateResolver,
                                  TemplateEngine templateEngine,
                                  ClassPathResourceLoader resourceLoader) {
        this.templateResolver = templateResolver;
        this.resourceLoader = resourceLoader;
        this.engine = templateEngine;
    }

    @Override
    @NonNull
    public Writable render(@NonNull String viewName,
                           @Nullable T data,
                           @NonNull HttpRequest<?> request) {
        ArgumentUtils.requireNonNull("viewName", viewName);
        ArgumentUtils.requireNonNull("request", request);
        return (writer) -> {
            IContext context = new WebContext(request, request.getLocale().orElse(Locale.US), ViewUtils.modelOf(data));
            render(viewName, context, writer);
        };
    }

    /**
     * Passes the arguments as is to {@link TemplateEngine#process(String, IContext, Writer)}.
     *
     * @param viewName The view name
     * @param context The context
     * @param writer The writer
     */
    public void render(String viewName, IContext context, Writer writer) {
        try {
            engine.process(viewName, context, writer);
        } catch (TemplateEngineException e) {
            throw new ViewRenderingException("Error rendering Thymeleaf view [" + viewName + "]: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean exists(@NonNull String viewName) {
        String location = viewLocation(viewName);
        return resourceLoader.getResourceAsStream(location).isPresent();
    }

    private TemplateEngine initializeTemplateEngine() {
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver);
        return engine;
    }

    private ClassLoaderTemplateResolver initializeTemplateResolver(ViewsConfiguration viewsConfiguration,
                                                                   ThymeleafViewsRendererConfiguration thConfiguration) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

        templateResolver.setPrefix(ViewUtils.normalizeFolder(viewsConfiguration.getFolder()));
        templateResolver.setCharacterEncoding(thConfiguration.getCharacterEncoding());
        templateResolver.setTemplateMode(thConfiguration.getTemplateMode());
        templateResolver.setSuffix(thConfiguration.getSuffix());
        templateResolver.setForceSuffix(thConfiguration.getForceSuffix());
        templateResolver.setForceTemplateMode(thConfiguration.getForceTemplateMode());
        templateResolver.setCacheTTLMs(thConfiguration.getCacheTTLMs());
        templateResolver.setCheckExistence(thConfiguration.getCheckExistence());
        templateResolver.setCacheable(thConfiguration.getCacheable());
        return templateResolver;
    }

    private String viewLocation(final String name) {
        return templateResolver.getPrefix() +
                ViewUtils.normalizeFile(name, templateResolver.getSuffix()) +
                templateResolver.getSuffix();
    }

}

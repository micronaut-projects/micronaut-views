/*
 * Copyright 2017-2023 original authors
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

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.io.ResourceLoader;
import io.micronaut.core.io.Writable;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.core.util.ArgumentUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.server.util.locale.HttpLocaleResolver;
import io.micronaut.views.ViewUtils;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.exceptions.ViewRenderingException;
import jakarta.inject.Singleton;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.ExpressionContext;
import org.thymeleaf.context.IContext;
import org.thymeleaf.exceptions.TemplateEngineException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.standard.expression.FragmentExpression;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.Writer;
import java.util.Locale;
import java.util.Set;

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
@Requires(classes = {HttpRequest.class, HttpLocaleResolver.class})
public class ThymeleafViewsRenderer<T> implements ViewsRenderer<T, HttpRequest<?>> {

    protected final AbstractConfigurableTemplateResolver templateResolver;
    protected final TemplateEngine engine;
    protected final HttpLocaleResolver httpLocaleResolver;
    protected ResourceLoader resourceLoader;

    /**
     * @param templateResolver   The template resolver
     * @param templateEngine     The template engine
     * @param resourceLoader     The resource loader
     * @param httpLocaleResolver The locale resolver
     */
    public ThymeleafViewsRenderer(AbstractConfigurableTemplateResolver templateResolver,
                                  TemplateEngine templateEngine,
                                  ClassPathResourceLoader resourceLoader,
                                  HttpLocaleResolver httpLocaleResolver) {
        this.templateResolver = templateResolver;
        this.resourceLoader = resourceLoader;
        this.engine = templateEngine;
        this.httpLocaleResolver = httpLocaleResolver;
    }

    @Override
    @NonNull
    public Writable render(@NonNull String viewName,
                           @Nullable T data,
                           @Nullable HttpRequest<?> request) {
        ArgumentUtils.requireNonNull("viewName", viewName);
        return (writer) -> {
            IContext context = new WebContext(request, request != null ? httpLocaleResolver.resolveOrDefault(request) : Locale.getDefault(),
                ViewUtils.modelOf(data));

            var templateAndFragment = resolveTemplate(viewName);

            render(templateAndFragment.templateName, templateAndFragment.fragmentSelectors, context, writer);
        };
    }

    /**
     * Passes the arguments as is to {@link TemplateEngine#process(String, IContext, Writer)}.
     *
     * @param viewName          The view name
     * @param fragmentSelectors Fragment selectors
     * @param context           The context
     * @param writer            The writer
     */
    public void render(String viewName, Set<String> fragmentSelectors, IContext context, Writer writer) {
        try {
            engine.process(viewName, fragmentSelectors, context, writer);
        } catch (TemplateEngineException e) {
            throw new ViewRenderingException("Error rendering Thymeleaf view [" + viewName + "]: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean exists(@NonNull String viewName) {
        var templateAndFragment = resolveTemplate(viewName);
        String location = viewLocation(templateAndFragment.templateName);
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

    private TemplateAndFragment resolveTemplate(String viewName) {
        if (!viewName.contains("::")) {
            return new TemplateAndFragment(viewName, null);
        }

        var expressionContext = new ExpressionContext(engine.getConfiguration());
        var parser = StandardExpressions.getExpressionParser(engine.getConfiguration());
        FragmentExpression fragmentExpression;
        try {
            fragmentExpression = (FragmentExpression) parser.parseExpression(expressionContext, "~{" + viewName + "}");
        } catch (TemplateProcessingException e) {
            throw new IllegalArgumentException("Invalid template name specification: '" + viewName + "'");
        }
        var fragment = FragmentExpression.createExecutedFragmentExpression(expressionContext, fragmentExpression);
        var templateName = FragmentExpression.resolveTemplateName(fragment);
        var fragmentSelectors = FragmentExpression.resolveFragments(fragment);

        return new TemplateAndFragment(templateName, fragmentSelectors);
    }

    private record TemplateAndFragment(String templateName, Set<String> fragmentSelectors) {
    }
}

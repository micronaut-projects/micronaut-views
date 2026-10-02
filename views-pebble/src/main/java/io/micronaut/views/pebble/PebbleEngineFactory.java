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
package io.micronaut.views.pebble;

import io.micronaut.context.annotation.Factory;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsSourceRoots;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.attributes.methodaccess.MethodAccessValidator;
import io.pebbletemplates.pebble.extension.Extension;
import io.pebbletemplates.pebble.extension.core.DisallowExtensionCustomizerBuilder;
import io.pebbletemplates.pebble.lexer.Syntax;
import io.pebbletemplates.pebble.loader.Loader;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

/**
 * Factory for PebbleEngine beans.
 *
 * @author Ecmel Ercan
 * @since 2.2.0
 */
@Factory
public class PebbleEngineFactory {

    private final ViewsConfiguration viewsConfiguration;
    private final PebbleConfiguration configuration;
    private final Optional<Loader<?>> loader;
    private final Optional<Syntax> syntax;
    private final Optional<MethodAccessValidator> methodAccessValidator;
    private final List<Extension> extensions;

    @Nullable
    private final ExecutorService executorService;
    private final ViewsSourceRoots sourceRoots;
    @Nullable
    private final ClassLoader classLoader;

    public PebbleEngineFactory(ViewsConfiguration viewsConfiguration,
                               PebbleConfiguration configuration,
                               Optional<Loader<?>> loader,
                               Optional<Syntax> syntax,
                               Optional<MethodAccessValidator> methodAccessValidator,
                               List<Extension> extensions) {
        this.viewsConfiguration = viewsConfiguration;
        this.configuration = configuration;
        this.loader = loader;
        this.syntax = syntax;
        this.methodAccessValidator = methodAccessValidator;
        this.extensions = extensions;
        this.executorService = null;
        this.sourceRoots = ViewsSourceRoots.none();
        this.classLoader = null;
    }

    /**
     * @param viewsConfiguration The views configuration
     * @param configuration The Pebble configuration
     * @param loader A loader bean, used instead of the default one
     * @param syntax The syntax
     * @param methodAccessValidator The method access validator
     * @param extensions The extensions
     * @param executorService The executor of the parallel tag
     * @deprecated Use {@link #PebbleEngineFactory(ViewsConfiguration, PebbleConfiguration, Optional, Optional, Optional, List, ExecutorService, ViewsSourceRoots, ClassPathResourceLoader)} instead.
     */
    @Deprecated(since = "6.4.0")
    public PebbleEngineFactory(ViewsConfiguration viewsConfiguration,
                               PebbleConfiguration configuration,
                               Optional<Loader<?>> loader,
                               Optional<Syntax> syntax,
                               Optional<MethodAccessValidator> methodAccessValidator,
                               List<Extension> extensions,
                               ExecutorService executorService) {
        this.viewsConfiguration = viewsConfiguration;
        this.configuration = configuration;
        this.loader = loader;
        this.syntax = syntax;
        this.methodAccessValidator = methodAccessValidator;
        this.extensions = extensions;
        this.executorService = executorService;
        this.sourceRoots = ViewsSourceRoots.none();
        this.classLoader = null;
    }

    /**
     * @param viewsConfiguration The views configuration
     * @param configuration The Pebble configuration
     * @param loader A loader bean, used instead of the default one
     * @param syntax The syntax
     * @param methodAccessValidator The method access validator
     * @param extensions The extensions
     * @param executorService The executor of the parallel tag
     * @param sourceRoots The views source roots, which the default loader reads ahead of the class path in development mode
     * @param resourceLoader The class path resource loader, whose class loader the default loader reads through in development mode
     * @since 6.4.0
     */
    @Inject
    public PebbleEngineFactory(ViewsConfiguration viewsConfiguration,
                               PebbleConfiguration configuration,
                               Optional<Loader<?>> loader,
                               Optional<Syntax> syntax,
                               Optional<MethodAccessValidator> methodAccessValidator,
                               List<Extension> extensions,
                               @Named(TaskExecutors.IO) ExecutorService executorService,
                               ViewsSourceRoots sourceRoots,
                               ClassPathResourceLoader resourceLoader) {
        this.viewsConfiguration = viewsConfiguration;
        this.configuration = configuration;
        this.loader = loader;
        this.syntax = syntax;
        this.methodAccessValidator = methodAccessValidator;
        this.extensions = extensions;
        this.executorService = executorService;
        this.sourceRoots = sourceRoots;
        this.classLoader = resourceLoader.getClassLoader();
    }

    /**
     * @return The Pebble Engine
     */
    @Singleton
    public PebbleEngine create() {
        PebbleEngine.Builder builder = new PebbleEngine.Builder()
            .registerExtensionCustomizer(new DisallowExtensionCustomizerBuilder()
                .disallowedTokenParserTags(List.of("include"))
                .build())
            .cacheActive(configuration.isCacheActive())
            .newLineTrimming(configuration.isNewLineTrimming())
            .autoEscaping(configuration.isAutoEscaping())
            .defaultEscapingStrategy(configuration.getDefaultEscapingStrategy())
            .strictVariables(configuration.isStrictVariables())
            .greedyMatchMethod(configuration.isGreedyMatchMethod())
            .allowOverrideCoreOperators(configuration.isAllowOverrideCoreOperators())
            .literalDecimalTreatedAsInteger(configuration.isLiteralDecimalsAsIntegers())
            .literalNumbersAsBigDecimals(configuration.isLiteralNumbersAsBigDecimals());


        if (executorService != null) {
            builder.executorService(executorService);
        }

        builder.loader(loader.orElseGet(() -> sourceRoots.isEnabled() && classLoader != null
            ? new PebbleLoader(viewsConfiguration, configuration, sourceRoots, classLoader)
            : new PebbleLoader(viewsConfiguration, configuration)));

        syntax.ifPresent(bean -> builder.syntax(bean));
        methodAccessValidator.ifPresent(bean -> builder.methodAccessValidator(bean));
        extensions.forEach(bean -> builder.extension(bean));

        // Not implemented:
        // defaultLocale, templateCache, tagCache, addEscapingStrategy

        return builder.build();
    }
}

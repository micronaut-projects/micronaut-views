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

import java.util.List;
import java.util.Optional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.PebbleEngine.Builder;
import com.mitchellbosecke.pebble.attributes.methodaccess.MethodAccessValidator;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.lexer.Syntax;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.Loader;
import io.micronaut.context.annotation.Factory;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsRenderer;
import java.util.concurrent.ExecutorService;
import jakarta.inject.Named;

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
    }

    @Inject
    public PebbleEngineFactory(ViewsConfiguration viewsConfiguration,
                               PebbleConfiguration configuration, 
                               Optional<Loader<?>> loader,
                               Optional<Syntax> syntax,
                               Optional<MethodAccessValidator> methodAccessValidator,
                               List<Extension> extensions,
                               @Named(TaskExecutors.IO) ExecutorService executorService) {
        this.viewsConfiguration = viewsConfiguration;
        this.configuration = configuration;
        this.loader = loader;
        this.syntax = syntax;
        this.methodAccessValidator = methodAccessValidator;
        this.extensions = extensions;
        this.executorService = executorService;
    }

    /**
     * @return The Pebble Engine
     */
    @Singleton
    public PebbleEngine create() {
        Builder builder = new PebbleEngine.Builder()
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

        if (loader.isPresent()) {
            builder.loader(loader.get());
        } else {
            Loader<?> loader = new ClasspathLoader();
            loader.setPrefix(viewsConfiguration.getFolder());
            loader.setSuffix(ViewsRenderer.EXTENSION_SEPARATOR + configuration.getDefaultExtension());
            builder.loader(loader);
        }

        syntax.ifPresent(bean -> builder.syntax(bean));
        methodAccessValidator.ifPresent(bean -> builder.methodAccessValidator(bean));
        extensions.forEach(bean -> builder.extension(bean));

        // Not implemented:
        // defaultLocale, templateCache, tagCache, addEscapingStrategy 
        
        return builder.build();
    }
}

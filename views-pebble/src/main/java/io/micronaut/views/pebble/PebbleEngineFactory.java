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

import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.PebbleEngine.Builder;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.lexer.Syntax;
import com.mitchellbosecke.pebble.loader.Loader;
import io.micronaut.context.annotation.Factory;

/**
 * Factory for PebbleEngine beans.
 *
 * @author Ecmel Ercan
 * @since 2.1.1
 */
@Factory
public class PebbleEngineFactory {

    private final PebbleConfiguration configuration;
    private final Optional<Loader<?>> loader;
    private final Optional<Syntax> syntax;
    private final Extension[] extensions;

    @Inject
    public PebbleEngineFactory(PebbleConfiguration configuration, 
                               Optional<Loader<?>> loader,
                               Optional<Syntax> syntax,
                               Extension[] extensions) {

        this.configuration = configuration;
        this.loader = loader;
        this.syntax = syntax;
        this.extensions = extensions;
    }

    /**
     * @return The Pebble Engine
     */
    @Singleton
    public PebbleEngine create() {
        Builder builder = new PebbleEngine.Builder()
            .autoEscaping(configuration.isAutoEscaping())
            .defaultEscapingStrategy(configuration.getDefaultEscapingStrategy())
            .strictVariables(configuration.isStrictVariables())
            .newLineTrimming(configuration.isNewLineTrimming())
            .cacheActive(configuration.isCacheActive())
            .greedyMatchMethod(configuration.isGreedyMatchMethod())
            .allowOverrideCoreOperators(configuration.isAllowOverrideCoreOperators())
            .literalDecimalTreatedAsInteger(configuration.isLiteralDecimalsAsIntegers())
            .literalNumbersAsBigDecimals(configuration.isLiteralNumbersAsBigDecimals());

        if (loader.isPresent()) {
            builder.loader(loader.get());
        }

        if (syntax.isPresent()) {
            builder.syntax(syntax.get());
        }

        if (extensions.length > 0) {
            builder.extension(extensions);
        }

        return builder.build();
    }
}

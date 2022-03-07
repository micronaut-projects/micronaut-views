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

import com.mitchellbosecke.pebble.PebbleEngine;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.io.Writable;
import io.micronaut.core.util.LocaleResolver;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.views.ViewUtils;
import io.micronaut.views.ViewsRenderer;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Locale;
import java.util.Optional;

/**
 * Renders Views with Pebble.
 *
 * @author Ecmel Ercan
 * @see <a href="https://pebbletemplates.io/">https://pebbletemplates.io/</a>
 * @since 2.2.0
 * @param <T> The model type
 */
@Singleton
@Requires(property = PebbleConfigurationProperties.ENABLED, notEquals = StringUtils.FALSE)
@Requires(classes = PebbleEngine.class)
public class PebbleViewsRenderer<T> implements ViewsRenderer<T> {

    private final PebbleEngine engine;
    private final LocaleResolver<HttpRequest<?>> httpLocaleResolver;

    /**
     * @param engine Pebble Engine
     * @param httpLocaleResolver The locale resolver
     */
    @Inject
    public PebbleViewsRenderer(PebbleEngine engine,
                               LocaleResolver<HttpRequest<?>> httpLocaleResolver) {
        this.engine = engine;
        this.httpLocaleResolver = httpLocaleResolver;
    }

    /**
     * @param engine Pebble Engine
     * @deprecated Use {@link #PebbleViewsRenderer(PebbleEngine, LocaleResolver)} instead.
     */
    @Deprecated
    public PebbleViewsRenderer(PebbleEngine engine) {
        this.engine = engine;
        this.httpLocaleResolver = new LocaleResolver<HttpRequest<?>>() {
            @Override @NonNull public Optional<Locale> resolve(@NonNull HttpRequest<?> context) {
                return context.getLocale();
            }

            @Override @NonNull public Locale resolveOrDefault(@NonNull HttpRequest<?> context) {
//                Returns US locale by default to simulate previous incorrect behavior to ensure it is non-breaking for
//                people relying on this behavior.
                return Locale.US;
            }
        };
    }

    /**
     * @param configuration Pebble Configuration
     * @param engine Pebble Engine
     * @deprecated Use {@link #PebbleViewsRenderer(PebbleEngine, LocaleResolver)} instead.
     */
    @Deprecated
    public PebbleViewsRenderer(PebbleConfiguration configuration, PebbleEngine engine) {
        this(engine);
    }

    @Override
    @NonNull
    public Writable render(@NonNull String name,
                           @Nullable T data,
                           @Nullable HttpRequest<?> request) {
        return (writer) -> engine.getTemplate(name).evaluate(writer, ViewUtils.modelOf(data), request != null ? httpLocaleResolver.resolveOrDefault(request) : Locale.getDefault());
    }

    @Override
    public boolean exists(@NonNull String name) {
        return engine.getLoader().resourceExists(name);
    }
}

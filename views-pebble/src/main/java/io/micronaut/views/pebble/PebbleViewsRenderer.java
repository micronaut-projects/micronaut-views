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

import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import com.mitchellbosecke.pebble.PebbleEngine;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.io.Writable;
import io.micronaut.core.util.StringUtils;
import io.micronaut.views.ViewUtils;
import io.micronaut.views.ViewsRenderer;

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

    @Inject
    public PebbleViewsRenderer(PebbleEngine engine) {    
        this.engine = engine;
    }

    @Override
    public Writable render(String name, T data, @NonNull HttpRequest<?> request) {
        return (writer) -> engine.getTemplate(name).evaluate(writer, ViewUtils.modelOf(data));
    }

    @Override
    public boolean exists(String name) {
        return engine.getLoader().resourceExists(name);
    }
}

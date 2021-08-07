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
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import io.micronaut.views.ViewUtils;
import io.micronaut.views.ViewsRenderer;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * Renders Views with Pebble.
 *
 * @author Ecmel Ercan
 * @see <a href="https://pebbletemplates.io/">https://pebbletemplates.io/</a>
 * @since 2.2.0
 */
@Singleton
@Produces(MediaType.TEXT_HTML)
@Requires(property = PebbleConfigurationProperties.ENABLED, notEquals = StringUtils.FALSE)
@Requires(classes = PebbleEngine.class)
public class PebbleViewsRenderer implements ViewsRenderer {

    private final String extension;
    private final PebbleEngine engine;

    @Inject
    public PebbleViewsRenderer(PebbleConfiguration configuration, PebbleEngine engine) {
        this.extension = configuration.getDefaultExtension();
        this.engine = engine;
    }

    @Override
    public @NonNull
    Writable render(@NonNull String name, @Nullable Object data) {
        return (writer) -> engine.getTemplate(ViewUtils.normalizeFile(name, extension)).evaluate(writer, modelOf(data));
    }

    @Override
    public boolean exists(@NonNull String name) {
        return engine.getLoader().resourceExists(ViewUtils.normalizeFile(name, extension));
    }
}

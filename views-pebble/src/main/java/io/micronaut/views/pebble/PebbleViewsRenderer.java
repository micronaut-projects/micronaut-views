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

import javax.inject.Inject;
import javax.inject.Singleton;
import com.mitchellbosecke.pebble.PebbleEngine;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.io.Writable;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import io.micronaut.views.ViewUtils;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsRenderer;

/**
 * Renders Views with with Pebble.
 *
 * @author Ecmel Ercan
 * @see <a href="https://pebbletemplates.io/">https://pebbletemplates.io/</a>
 * @since 2.1.1
 */
@Singleton
@Produces(MediaType.TEXT_HTML)
@Requires(property = PebbleConfigurationProperties.ENABLED, notEquals = StringUtils.FALSE)
@Requires(classes = PebbleEngine.class)
public class PebbleViewsRenderer implements ViewsRenderer {
    
    private final PebbleEngine engine;
    private final String extension;
    private final String folder;

    @Inject
    public PebbleViewsRenderer(ViewsConfiguration viewsConfiguration,
                               PebbleConfiguration configuration,
                               PebbleEngine engine) {
        
        this.folder = viewsConfiguration.getFolder();
        this.extension = configuration.getDefaultExtension();
        this.engine = engine;
    }

    @Override
    public Writable render(String name, Object data) {
        return (writer) -> engine.getTemplate(location(name)).evaluate(writer, modelOf(data));
    }

    @Override
    public boolean exists(String name) {
        return engine.getLoader().resourceExists(location(name));
    }

    private String location(String name) {
        return folder + ViewUtils.normalizeFile(name, extension) + EXTENSION_SEPARATOR + extension;
    }
}

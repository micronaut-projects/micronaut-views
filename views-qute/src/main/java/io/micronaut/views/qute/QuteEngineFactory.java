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
package io.micronaut.views.qute;

import io.micronaut.context.annotation.Factory;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.views.ViewsConfiguration;
import io.quarkus.qute.Engine;
import io.quarkus.qute.EngineBuilder;
import io.quarkus.qute.HtmlEscaper;
import io.quarkus.qute.Variant;
import jakarta.inject.Singleton;

import java.util.List;

/**
 * Factory for Qute beans.
 *
 * @author Sergio del Amo
 * @since 6.1.0
 */
@Factory
public class QuteEngineFactory {

    private static final String APPLICATION_XHTML = "application/xhtml+xml";

    /**
     * @param resourceLoader Classpath resource loader
     * @param viewsConfiguration Views configuration
     * @param quteConfiguration Qute views configuration
     * @return The Qute template locator
     */
    @Singleton
    public QuteTemplateLocator quteTemplateLocator(ClassPathResourceLoader resourceLoader,
                                                   ViewsConfiguration viewsConfiguration,
                                                   QuteViewsRendererConfiguration quteConfiguration) {
        return new QuteTemplateLocator(resourceLoader, viewsConfiguration, quteConfiguration);
    }

    /**
     * @param quteConfiguration Qute views configuration
     * @param templateLocator Qute template locator
     * @return The Qute engine
     */
    @Singleton
    public Engine quteEngine(QuteViewsRendererConfiguration quteConfiguration,
                             QuteTemplateLocator templateLocator) {
        EngineBuilder builder = Engine.builder()
            .addDefaults()
            .addLocator(templateLocator)
            .strictRendering(quteConfiguration.isStrictRendering());
        if (quteConfiguration.isHtmlEscaping()) {
            builder.addResultMapper(new HtmlEscaper(List.of(Variant.TEXT_HTML, APPLICATION_XHTML)));
        }
        return builder.build();
    }
}

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
package io.micronaut.views.velocity;

import io.micronaut.context.annotation.Factory;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsSourceRoots;
import org.apache.velocity.app.VelocityEngine;

import jakarta.inject.Singleton;
import java.util.Properties;

/**
 * Factory for the velocity engine.
 *
 * @author James Kleeh
 * @since 1.1.0
 */
@Factory
public class VelocityFactory {

    /**
     * @return The velocity engine
     * @deprecated Use {@link #getVelocityEngine(ViewsConfiguration, ViewsSourceRoots, ClassPathResourceLoader)} instead.
     */
    @Deprecated(since = "6.4.0")
    public VelocityEngine getVelocityEngine() {
        final Properties p = new Properties();
        p.setProperty("resource.loaders", "class");
        p.setProperty("resource.loader.class.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        return new VelocityEngine(p);
    }

    /**
     * In development mode, the engine reads the templates of the views folder from the views source roots ahead of
     * the class path, and the class path through the application's class loader. Otherwise it reads them from the
     * class path, as before.
     *
     * @param viewsConfiguration The views configuration
     * @param sourceRoots The views source roots
     * @param resourceLoader The class path resource loader
     * @return The velocity engine
     * @since 6.4.0
     */
    @Singleton
    @SuppressWarnings("deprecation")
    public VelocityEngine getVelocityEngine(ViewsConfiguration viewsConfiguration,
                                            ViewsSourceRoots sourceRoots,
                                            ClassPathResourceLoader resourceLoader) {
        if (!sourceRoots.isEnabled()) {
            return getVelocityEngine();
        }
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty("resource.loaders", "source");
        engine.setProperty("resource.loader.source.instance",
            new SourceRootsResourceLoader(viewsConfiguration.getFolder(), sourceRoots, resourceLoader.getClassLoader()));
        return engine;
    }
}

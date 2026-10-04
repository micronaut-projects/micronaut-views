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
package io.micronaut.views.rocker;

import com.fizzed.rocker.runtime.RockerRuntime;
import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsSourceRoots;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Singleton;

/**
 * Factory for the Rocker engine.
 *
 * @author Sam Adams
 * @since 1.3.2
 */
@Factory
public class RockerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(RockerFactory.class);

    /**
     * @param viewsConfiguration The views configuration
     * @param rockerConfiguration The Rocker configuration
     * @return The Rocker engine
     * @deprecated Use {@link #rockerEngine(ViewsConfiguration, RockerViewsRendererConfiguration, ViewsSourceRoots, BeanContext)} instead.
     */
    @Deprecated(since = "6.4.0")
    public RockerEngine rockerEngine(ViewsConfiguration viewsConfiguration,
                                     RockerViewsRendererConfiguration rockerConfiguration) {
        RockerRuntime.getInstance().setReloading(rockerConfiguration.isHotReloading());
        return new RockerEngine(viewsConfiguration.getFolder(), rockerConfiguration.getDefaultExtension());
    }

    /**
     * In development mode, the templates of the views source roots are compiled at runtime and compiled again
     * after a change, and the classes the build generated are loaded through the application's class loader.
     * Rocker's own hot reloading stays off then: {@code RockerRuntime} is one per process, in the library tier,
     * and its reloading cannot load the classes of the application. Otherwise the engine is the one of before, and
     * the deprecated {@code micronaut.views.rocker.hot-reloading} turns on Rocker's own reloading.
     *
     * @param viewsConfiguration The views configuration
     * @param rockerConfiguration The Rocker configuration
     * @param sourceRoots The views source roots
     * @param beanContext The context, whose class loader is the application's
     * @return The Rocker engine
     * @since 6.4.0
     */
    @Singleton
    @Bean(preDestroy = "close")
    @SuppressWarnings("deprecation")
    public RockerEngine rockerEngine(ViewsConfiguration viewsConfiguration,
                                     RockerViewsRendererConfiguration rockerConfiguration,
                                     ViewsSourceRoots sourceRoots,
                                     BeanContext beanContext) {
        if (sourceRoots.isEnabled()) {
            if (rockerConfiguration.isHotReloading()) {
                LOG.info("micronaut.views.rocker.hot-reloading is deprecated and has no effect in development mode, where Rocker templates of the views source roots are compiled after an edit");
            }
            // the runtime is shared by every generation of the process: keep its plain bootstrap, which finds a
            // template's class through the loader of the model, the generation's or the runtime compilation's
            RockerRuntime.getInstance().setReloading(false);
            return new RockerEngine(viewsConfiguration.getFolder(), rockerConfiguration.getDefaultExtension(), sourceRoots, beanContext.getClassLoader());
        }
        RockerRuntime.getInstance().setReloading(rockerConfiguration.isHotReloading());
        return new RockerEngine(viewsConfiguration.getFolder(), rockerConfiguration.getDefaultExtension());
    }

}

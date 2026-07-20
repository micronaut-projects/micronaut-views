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
package io.micronaut.views.jinjava;

import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

/**
 * Creates the Jinjava engine used by Views.
 *
 * @since 6.1.1
 */
@Factory
final class JinjavaFactory {

    /**
     * @param jinjavaConfig Jinjava configuration
     * @param resourceLocator The scoped Jinjava resource locator
     * @return The configured Jinjava engine
     */
    @Singleton
    Jinjava jinjava(JinjavaConfig jinjavaConfig,
                    JinjavaResourceLocator resourceLocator) {
        Jinjava jinjava = new Jinjava(jinjavaConfig);
        jinjava.setResourceLocator(resourceLocator);
        return jinjava;
    }

    /**
     * @param configuration Jinjava Views configuration
     * @return JinjavaConfig
     */
    @Singleton
    JinjavaConfig jinjavaConfig(JinjavaViewsRendererConfigurationProperties configuration) {
        return configuration.getConfig().build();
    }
}

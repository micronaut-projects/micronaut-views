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

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.views.ViewsConfigurationProperties;

/**
 * {@link ConfigurationProperties} implementation of {@link VelocityViewsRendererConfiguration}.
 *
 * @author Sergio del Amo
 * @since 1.0
 */
@ConfigurationProperties(VelocityViewsRendererConfigurationProperties.PREFIX)
public class VelocityViewsRendererConfigurationProperties implements VelocityViewsRendererConfiguration {

    public static final String PREFIX = ViewsConfigurationProperties.PREFIX + ".velocity";

    /**
     * The default extension.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String DEFAULT_EXTENSION = "vm";

    /**
     * The default enable value.
     */
    @SuppressWarnings("WeakerAccess")
    public static final boolean DEFAULT_ENABLED = true;

    private boolean enabled = DEFAULT_ENABLED;

    private String defaultExtension = DEFAULT_EXTENSION;

    /**
     * Whether velocity views are enabled. Default value ({@value #DEFAULT_ENABLED}).
     *
     * @return boolean flag indicating whether {@link VelocityViewsRenderer} is enabled.
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * The default extension to use for velocity templates. Default value ({@value #DEFAULT_EXTENSION}).
     *
     * @return Default extension for templates. By default {@value #DEFAULT_EXTENSION}.
     */
    @Override
    public String getDefaultExtension() {
        return defaultExtension;
    }

    /**
     * Whether velocity views are enabled.
     *
     * @param enabled True if they are
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Sets the default extension to use for velocity templates.
     *
     * @param defaultExtension The default extension
     */
    public void setDefaultExtension(String defaultExtension) {
        this.defaultExtension = defaultExtension;
    }
}

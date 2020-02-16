/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.views.rocker;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.views.ViewsConfigurationProperties;

/**
 * {@link ConfigurationProperties} implementation of {@link RockerViewsRendererConfiguration}.
 *
 * @author Sam Adams
 * @since 1.3.2
 */
@ConfigurationProperties(RockerViewsRendererConfigurationProperties.PREFIX)
public class RockerViewsRendererConfigurationProperties implements RockerViewsRendererConfiguration {

    public static final String PREFIX = ViewsConfigurationProperties.PREFIX + ".rocker";

    /**
     * The default extension.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String DEFAULT_EXTENSION = "rocker.html";

    /**
     * The default hot reloading value.
     */
    @SuppressWarnings("WeakerAccess")
    public static final boolean DEFAULT_HOT_RELOADING = false;

    /**
     * The default relaxed value.
     */
    @SuppressWarnings("WeakerAccess")
    public static final boolean DEFAULT_RELAXED = false;

    /**
     * The default enable value.
     */
    @SuppressWarnings("WeakerAccess")
    public static final boolean DEFAULT_ENABLED = true;

    private boolean enabled = DEFAULT_ENABLED;
    private String defaultExtension = DEFAULT_EXTENSION;
    private boolean hotReloading = DEFAULT_HOT_RELOADING;
    private boolean relaxed = DEFAULT_RELAXED;

    /**
     * Enabled getter.
     *
     * @return boolean flag indicating whether {@link RockerViewsRenderer} is enabled.
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * @return Default extension for templates. By default {@value #DEFAULT_EXTENSION}.
     */
    @Override
    public String getDefaultExtension() {
        return defaultExtension;
    }

    /**
     * Hot reloading getter.
     *
     * @return boolean flag indicating whether hot reloading is enabled.
     */
    @Override
    public boolean isHotReloading() {
        return hotReloading;
    }

    /**
     * Relaxed binding getter.
     *
     * @return boolean flag indicating whether relaxed binding is enabled for dynamic templates.
     */
    @Override
    public boolean isRelaxed() {
        return relaxed;
    }
    
    /**
     * Whether Rocker views are enabled. Default value ({@value #DEFAULT_ENABLED}).
     *
     * @param enabled True if they are
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Sets the default extension to use for Rocker templates. Default value ({@value #DEFAULT_EXTENSION}).
     *
     * @param defaultExtension The default extension
     */
    public void setDefaultExtension(String defaultExtension) {
        this.defaultExtension = defaultExtension;
    }

    /**
     * Whether hot reloading is enabled. Default value ({@value #DEFAULT_HOT_RELOADING}).
     *
     * @param hotReloading True if it is
     */
    public void setHotReloading(boolean hotReloading) {
        this.hotReloading = hotReloading;
    }

    /**
     * Whether relaxed binding is enabled for dynamic templates. Default value ({@value #DEFAULT_RELAXED}).
     *
     * @param relaxed True if it is
     */
    public void setRelaxed(boolean relaxed) {
        this.relaxed = relaxed;
    }
}

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

import com.hubspot.jinjava.JinjavaConfig;
import io.micronaut.context.annotation.ConfigurationBuilder;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.util.StringUtils;
import io.micronaut.core.util.Toggleable;
import io.micronaut.views.ViewsConfigurationProperties;

/**
 * Configuration properties for Jinjava Views rendering.
 *
 * @since 6.1.1
 */
@ConfigurationProperties(JinjavaViewsRendererConfigurationProperties.PREFIX)
public final class JinjavaViewsRendererConfigurationProperties implements Toggleable {

    /** The Jinjava Views configuration prefix. */
    public static final String PREFIX = ViewsConfigurationProperties.PREFIX + ".jinja";
    /** Whether Jinjava Views rendering is enabled by default. */
    public static final boolean DEFAULT_ENABLED = true;
    /** The default Jinjava template extension. */
    public static final String DEFAULT_EXTENSION = "jinja";

    private boolean enabled = DEFAULT_ENABLED;
    private String defaultExtension = DEFAULT_EXTENSION;

    @ConfigurationBuilder(prefixes = "with", configurationPrefix = "config")
    private final JinjavaConfig.Builder config = JinjavaConfig.newBuilder();

    /**
     * @return Whether Jinjava Views rendering is enabled
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled Whether Jinjava Views rendering is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return The default template extension
     */
    public String getDefaultExtension() {
        return defaultExtension;
    }

    /**
     * @param defaultExtension The default template extension
     */
    public void setDefaultExtension(String defaultExtension) {
        if (StringUtils.isNotEmpty(defaultExtension)) {
            this.defaultExtension = defaultExtension;
        }
    }

    /**
     * @return The Jinjava configuration builder
     */
    public JinjavaConfig.Builder getConfig() {
        return config;
    }

    /**
     * @return The configured Jinjava configuration
     */
    public JinjavaConfig build() {
        return config.build();
    }
}

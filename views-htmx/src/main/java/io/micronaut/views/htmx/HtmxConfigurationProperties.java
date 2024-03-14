/*
 * Copyright 2017-2024 original authors
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
package io.micronaut.views.htmx;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.views.ViewsConfigurationProperties;

/**
 * {@link ConfigurationProperties} implementation of {@link HtmxConfiguration}.
 * @author Sergio del Amo
 * @since 6.0.0
 */
@Requires(property = HtmxConfigurationProperties.PREFIX + ".enabled", notEquals = StringUtils.FALSE, defaultValue = StringUtils.TRUE)
@ConfigurationProperties(HtmxConfigurationProperties.PREFIX)
public class HtmxConfigurationProperties implements HtmxConfiguration {
    /**
     * The default enable value.
     */
    @SuppressWarnings("WeakerAccess")
    public static final boolean DEFAULT_ENABLED = true;

    /**
     * The prefix for htmx configuration.
     */
    public static final String PREFIX = ViewsConfigurationProperties.PREFIX + ".htmx";

    private boolean enabled = DEFAULT_ENABLED;

    /**
     * Whether the turbo integration is enabled. Default value ({@value #DEFAULT_ENABLED}).
     *
     * @return boolean flag indicating whether the turbo integration is enabled.
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Whether the turbo integration is enabled. Default value ({@value #DEFAULT_ENABLED}).
     *
     * @param enabled True if is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

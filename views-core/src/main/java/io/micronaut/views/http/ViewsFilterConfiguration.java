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
package io.micronaut.views.http;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.util.Toggleable;
import io.micronaut.views.ViewsConfigurationProperties;

/**
 * Configuration for the views filter.
 */
@ConfigurationProperties(ViewsFilterConfiguration.PREFIX)
public class ViewsFilterConfiguration implements Toggleable {

    public static final String PREFIX = ViewsConfigurationProperties.PREFIX + ".filter";
    public static final boolean DEFAULT_ENABLED = true;
    public static final String DEFAULT_ENABLED_AS_STRING = DEFAULT_ENABLED ? "true" : "false";
    private boolean enabled = DEFAULT_ENABLED;

    /**
     * Whether the views filter is enabled. Default value ({@value #DEFAULT_ENABLED}).
     *
     * @return True if the views filter is enabled
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether the filter is enabled.
     *
     * @param enabled True if the filter is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

/*
 * Copyright 2017-2023 original authors
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
package io.micronaut.views.fields.render;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.annotation.Internal;
import io.micronaut.views.ViewsConfigurationProperties;

/**
 * {@link ConfigurationProperties} implementation of {@link FormElementRendererConfiguration}.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Internal
@ConfigurationProperties(FormElementRendererConfigurationProperties.PREFIX)
public class FormElementRendererConfigurationProperties implements FormElementRendererConfiguration {

    /**
     * The prefix for form element rendering.
     */
    public static final String PREFIX = ViewsConfigurationProperties.PREFIX + ".form-element.render";

    /**
     * The default enable value.
     */
    @SuppressWarnings("WeakerAccess")
    public static final boolean DEFAULT_ENABLED = true;

    private boolean enabled = DEFAULT_ENABLED;

    /**
     * Whether form element rendering is enabled. Default value ({@value #DEFAULT_ENABLED}).
     *
     * @return boolean flag indicating whether {@link FormElementRenderer} is enabled.
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Whether form element rendering is enabled. Default value ({@value #DEFAULT_ENABLED}).
     *
     * @param enabled True if they are
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

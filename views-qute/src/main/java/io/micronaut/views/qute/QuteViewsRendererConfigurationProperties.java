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

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.util.StringUtils;
import io.micronaut.views.ViewsConfigurationProperties;

/**
 * {@link ConfigurationProperties} implementation of {@link QuteViewsRendererConfiguration}.
 *
 * @author Sergio del Amo
 * @since 6.1.0
 */
@ConfigurationProperties(QuteViewsRendererConfigurationProperties.PREFIX)
public class QuteViewsRendererConfigurationProperties implements QuteViewsRendererConfiguration {

    public static final String PREFIX = ViewsConfigurationProperties.PREFIX + ".qute";
    public static final String ENABLED = PREFIX + ".enabled";

    public static final boolean DEFAULT_ENABLED = true;
    public static final String DEFAULT_EXTENSION = "html";
    public static final boolean DEFAULT_STRICT_RENDERING = false;
    public static final boolean DEFAULT_HTML_ESCAPING = true;

    private boolean enabled = DEFAULT_ENABLED;
    private String defaultExtension = DEFAULT_EXTENSION;
    private boolean strictRendering = DEFAULT_STRICT_RENDERING;
    private boolean htmlEscaping = DEFAULT_HTML_ESCAPING;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether Qute view rendering is enabled. Default value ({@value #DEFAULT_ENABLED}).
     *
     * @param enabled Whether Qute view rendering is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String getDefaultExtension() {
        return defaultExtension;
    }

    /**
     * The default Qute template extension. Default value ({@value #DEFAULT_EXTENSION}).
     *
     * @param defaultExtension The default extension
     */
    public void setDefaultExtension(String defaultExtension) {
        if (StringUtils.isNotEmpty(defaultExtension)) {
            this.defaultExtension = defaultExtension;
        }
    }

    @Override
    public boolean isStrictRendering() {
        return strictRendering;
    }

    /**
     * Sets whether Qute strict rendering is enabled. Default value ({@value #DEFAULT_STRICT_RENDERING}).
     *
     * @param strictRendering Whether strict rendering is enabled
     */
    public void setStrictRendering(boolean strictRendering) {
        this.strictRendering = strictRendering;
    }

    @Override
    public boolean isHtmlEscaping() {
        return htmlEscaping;
    }

    /**
     * Sets whether HTML escaping is enabled for HTML templates. Default value ({@value #DEFAULT_HTML_ESCAPING}).
     *
     * @param htmlEscaping Whether HTML escaping is enabled
     */
    public void setHtmlEscaping(boolean htmlEscaping) {
        this.htmlEscaping = htmlEscaping;
    }
}

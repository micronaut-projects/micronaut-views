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
package io.micronaut.views.react;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.bind.annotation.Bindable;
import io.micronaut.views.ViewsConfigurationProperties;
import jakarta.validation.constraints.NotBlank;

/**
 * An interface to the configuration properties for React server-side rendering.
 */
@ConfigurationProperties(ReactViewsRendererConfiguration.PREFIX)
public interface ReactViewsRendererConfiguration {
    /**
     * The config key prefix used to configure the React SSR view renderer.
     */
    String PREFIX = ViewsConfigurationProperties.PREFIX + ".react";

    /** The default value for {@link #getClientBundleURL()}. */
    String DEFAULT_CLIENT_BUNDLE_URL = "/static/client.js";

    /** The default value for {@link #getServerBundlePath()}. */
    String DEFAULT_SERVER_BUNDLE_PATH = "classpath:views/ssr-components.mjs";

    /** The default value for {@link #getRenderScript()}. */
    String DEFAULT_RENDER_SCRIPT = "classpath:io/micronaut/views/react/react.js";

    /**
     * @return the URL (relative or absolute) where the client Javascript bundle can be found. It will
     * be appended to the generated HTML in a &lt;script&gt; tag. Defaults
     * to {@value #DEFAULT_CLIENT_BUNDLE_URL}
     */
    @NotBlank
    @NonNull
    @Bindable(defaultValue = DEFAULT_CLIENT_BUNDLE_URL)
    @SuppressWarnings("unused")  // Accessed from Javascript via reflection.
    String getClientBundleURL();

    /**
     * @return the path relative to micronaut.views.folder where the bundle used for
     * server-side rendering can be found. Defaults to {@value #DEFAULT_SERVER_BUNDLE_PATH}
     */
    @NotBlank
    @NonNull
    @Bindable(defaultValue = DEFAULT_SERVER_BUNDLE_PATH)
    String getServerBundlePath();


    /**
     * @return Either a file path (starting with "file:" or a resource in the classpath
     * (starting with "classpath:") to a render script. Please see the user guide for
     * more information on what this Javascript file should contain.
     */
    @NotBlank
    @NonNull
    @Bindable(defaultValue = DEFAULT_RENDER_SCRIPT)
    String getRenderScript();

    /**
     * @return If true, GraalJS sandboxing is enabled. This helps protect you against supply
     * chain attacks that might inject code into your server via hijacked React components.
     * It requires a sufficiently new version of GraalJS. Defaults to OFF.
     */
    @Bindable(defaultValue = "false")
    boolean getSandbox();
}

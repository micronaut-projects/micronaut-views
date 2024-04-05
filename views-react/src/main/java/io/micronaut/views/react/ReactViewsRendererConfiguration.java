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

    /** The default value for {@link #getClientBundleURL()} */
    String DEFAULT_CLIENT_BUNDLE_URL = "/static/client.js";

    /** The default value for {@link #getServerBundlePath()} */
    String DEFAULT_SERVER_BUNDLE_PATH = "ssr-components.mjs";

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
}

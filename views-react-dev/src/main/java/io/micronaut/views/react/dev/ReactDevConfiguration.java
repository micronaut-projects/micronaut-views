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
package io.micronaut.views.react.dev;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.bind.annotation.Bindable;

/**
 * Refreshing the browser when the React server bundle is rebuilt.
 *
 * <p>{@code micronaut-views-react} already reloads the bundle in process: it watches the file,
 * drops its pool of GraalJS contexts, and the next render uses the rebuild. Nothing told the
 * browser, so the refresh was still manual. This module closes that gap.
 *
 * <p>Two things have to be true before any of it is registered. The application must be running in
 * the {@value Environment#DEVELOPMENT} environment, and {@code micronaut.views.react.dev.enabled}
 * must be set -- {@code config/application-dev.toml} is the natural home for it. Declare the module
 * itself as a development-only dependency so it is not on the classpath of a production build at
 * all.
 */
@Requires(env = Environment.DEVELOPMENT)
@Requires(property = ReactDevConfiguration.PREFIX + ".enabled", value = "true")
@ConfigurationProperties(ReactDevConfiguration.PREFIX)
public interface ReactDevConfiguration {
    /**
     * The prefix for development reload configuration.
     */
    String PREFIX = "micronaut.views.react.dev";

    /**
     * The default value for {@link #getPath()}.
     */
    String DEFAULT_PATH = "/micronaut/views/react/dev-reload";

    /**
     * Whether to tell the browser when the server bundle has been rebuilt.
     *
     * <p>Off by default. It serves an unauthenticated endpoint and adds a script to every rendered
     * page, so it has to be asked for rather than inferred.
     *
     * @return whether browser refresh on rebuild is enabled. Defaults to {@code false}.
     */
    @Bindable(defaultValue = "false")
    boolean isEnabled();

    /**
     * @return the path that both transports are served from, and that the injected script connects
     * to. Defaults to {@value #DEFAULT_PATH}.
     */
    @NonNull
    @Bindable(defaultValue = DEFAULT_PATH)
    String getPath();

}

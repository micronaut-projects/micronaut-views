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
package io.micronaut.views.rocker;

import io.micronaut.views.ViewsRendererConfiguration;

/**
 * Configuration for {@link RockerViewsRenderer}.
 *
 * @author Sam Adams
 * @since 1.3.2
 */
public interface RockerViewsRendererConfiguration extends ViewsRendererConfiguration {
    /**
     * @return If hot reloading is enabled
     * @deprecated Rocker's own hot reloading, outside development mode. Under the development launcher, the
     * templates of the views source roots are compiled after an edit without it.
     */
    @Deprecated(since = "6.4.0")
    boolean isHotReloading();

    /**
     * @return If relaxed binding is enabled for dynamic templates
     */
    boolean isRelaxed();

}

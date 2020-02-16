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

import io.micronaut.core.util.Toggleable;

/**
 * Configuration for {@link RockerViewsRenderer}.
 *
 * @author Sam Adams
 * @since 1.3.2
 */
public interface RockerViewsRendererConfiguration extends Toggleable {

    /**
     * @return Default extension for templates
     */
    String getDefaultExtension();
    
    /**
     * @return If hot reloading is enabled
     */
    boolean isHotReloading();

    /**
     * @return If relaxed binding is enabled for dynamic templates
     */
    boolean isRelaxed();
    
    /**
     * @return Default path for templates
     */
    String getDefaultPath();
}

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

import com.fizzed.rocker.runtime.RockerRuntime;
import io.micronaut.context.annotation.Factory;

import javax.inject.Singleton;

/**
 * Factory for the Rocker engine.
 *
 * @author Sam Adams
 */
@Factory
public class RockerFactory {

    /**
     * @param rockerConfiguration The Rocker configuration
     * @return The Rocker engine
     */
    @Singleton
    public RockerEngine rockerEngine(RockerViewsRendererConfiguration rockerConfiguration) {
        RockerRuntime.getInstance().setReloading(rockerConfiguration.isHotReloading());
        return new RockerEngine(rockerConfiguration.getDefaultPath(), rockerConfiguration.getDefaultExtension());
    }

}

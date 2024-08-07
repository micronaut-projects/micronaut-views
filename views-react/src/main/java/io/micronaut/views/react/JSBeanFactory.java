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
package io.micronaut.views.react;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.core.annotation.Internal;
import org.graalvm.polyglot.HostAccess;

/**
 * Allows the default Javascript context and host access policy to be controlled.
 */
@Factory
@Internal
class JSBeanFactory {
    /**
     * This defaults to
     * {@link HostAccess#ALL} if the sandbox is disabled, or {@link HostAccess#CONSTRAINED} if it's on.
     * By replacing the {@link HostAccess} bean you can whitelist methods/properties by name or
     * annotation, which can be useful for exposing third party libraries where you can't add the
     * normal {@link HostAccess.Export} annotation, or allowing sandboxed JS to extend or implement
     * Java types.
     */
    @Singleton
    HostAccess hostAccess(ReactViewsRendererConfiguration configuration) {
        if (configuration.getSandbox()) {
            return HostAccess.CONSTRAINED;
        } else {
            return HostAccess.ALL;
        }
    }
}

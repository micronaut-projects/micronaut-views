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
package io.micronaut.views.react;

import io.micronaut.core.annotation.Experimental;
import org.graalvm.polyglot.Context;

import java.util.function.Function;

/**
 * Provides callback-scoped access to a context capable of executing JavaScript.
 *
 * <p>The callback must not return the context or context-owned values to application code.
 * Renderer implementations may associate context-bound caches with the context, but providers
 * retain ownership and providers that do not own their contexts must leave them open when sources
 * change.</p>
 *
 * @author Micronaut Team
 * @since 6.3.0
 */
@Experimental
@FunctionalInterface
public interface ReactContextProvider {

    /**
     * Execute a callback with exclusive access to a context.
     *
     * @param callback The callback
     * @param <T> The callback result type
     * @return The callback result
     */
    <T> T withContext(Function<Context, T> callback);

    /**
     * Notify the provider that React sources have changed.
     *
     * @param generation The new source generation
     */
    default void sourcesChanged(long generation) {
    }
}

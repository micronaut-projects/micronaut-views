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

import io.micronaut.core.annotation.Internal;
import io.micronaut.views.react.util.BeanPool;
import org.graalvm.polyglot.Context;

import java.util.function.Function;

/**
 * Owns the standalone React context pool used when an application does not supply a
 * {@link ReactContextProvider}. Source changes invalidate the whole pool because its contexts
 * belong exclusively to Views React and their module state cannot be reused safely.
 */
@Internal
final class DefaultReactContextProvider implements ReactContextProvider {
    private final BeanPool<ReactJSContext> contextPool;

    DefaultReactContextProvider(BeanPool<ReactJSContext> contextPool) {
        this.contextPool = contextPool;
    }

    @Override
    public <T> T withContext(Function<Context, T> callback) {
        return contextPool.useContext(handle -> callback.apply(handle.get().polyglotContext()));
    }

    @Override
    public void sourcesChanged(long generation) {
        contextPool.clear();
    }
}

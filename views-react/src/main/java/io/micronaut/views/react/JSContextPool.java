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

import io.micronaut.context.ApplicationContext;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.lang.ref.SoftReference;
import java.util.LinkedList;

/**
 * Vends contexts to threads that need them. We don't use ThreadLocals here because what matters
 * is contention. If there are 30 server threads, but only a few requests ever use React, then
 * we don't want to have 30 contexts in memory at all times because they are quite chunky objects.
 * By only creating more when we are genuinely under load, we avoid bloat. This also fits better
 * with virtual threads, where a thread may not live beyond the lifetime of a single request.
 */
@Singleton
class JSContextPool {
    private final ApplicationContext applicationContext;

    // Synchronized on JSContextPool.
    private final LinkedList<SoftReference<JSContext>> contexts = new LinkedList<>();

    @Inject
    JSContextPool(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Returns a cached context or creates a new one. You must give the JSContext to
     * {@link #release(JSContext)} when you're done with it to put it (back) into the pool.
     */
    JSContext acquire() {
        synchronized (this) {
            while (!contexts.isEmpty()) {
                SoftReference<JSContext> ref = contexts.poll();
                assert ref != null;

                var context = ref.get();
                // context may have been garbage collected (== null).
                if (context != null) {
                    return context;
                }
            }
        }

        // No more pooled contexts available, create one and return it. It'll be added to the
        // pool when released. No need to hold the lock whilst doing that.
        return applicationContext.createBean(JSContext.class);
    }

    synchronized void release(JSContext jsContext) {
        // Put it back into the pool for reuse.
        contexts.add(new SoftReference<>(jsContext));
    }
}

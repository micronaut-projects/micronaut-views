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
import io.micronaut.core.annotation.Internal;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
@Internal
class JSContextPool {
    private final ApplicationContext applicationContext;

    // Synchronized on 'this'.
    private final LinkedList<SoftReference<JSContext>> contexts = new LinkedList<>();
    private int versionCounter = 0;   // File reloads.

    @Inject
    JSContextPool(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Returns a cached context or creates a new one. You must give the JSContext to
     * {@link #release(JSContext)} when you're done with it to put it (back) into the pool.
     */
    synchronized JSContext acquire() {
        while (!contexts.isEmpty()) {
            SoftReference<JSContext> ref = contexts.poll();
            assert ref != null;

            var context = ref.get();
            // context may have been garbage collected (== null), or it might be for an old
            // version of the on-disk bundle. In both cases we just let it drift away as we
            // now hold the only reference.
            if (context != null && context.versionCounter == versionCounter) {
                return context;
            }
        }

        // No more pooled contexts available, create one and return it. It'll be added [back] to the
        // pool when release() is called.
        return applicationContext.createBean(JSContext.class, versionCounter);
    }

    /**
     * Puts a context back into the pool. It should be returned in a 'clean' state, so whatever
     * thread picks it up next finds it ready to use and without any leftover data from prior
     * usages.
     */
    synchronized void release(JSContext jsContext) {
        // Put it back into the pool for reuse unless it's out of date, in which case just let it drift.
        if (jsContext.versionCounter == versionCounter)
            contexts.add(new SoftReference<>(jsContext));
    }

    /**
     * Semantically this method empties the pool. The actual contexts won't be released until they
     * are requested later, this implementation just marks them as out of date. Out of date contexts
     * won't be re-added to the pool even if {@link #release(JSContext)} is called on them. It can
     * be used if there is a need to reload all the contexts, e.g. because a file on disk changed.
     */
    synchronized void releaseAll() {
        versionCounter++;
        if (versionCounter < 0)
            throw new IllegalStateException("Version counter wrapped, you can't call releaseAll this many times.");
    }
}

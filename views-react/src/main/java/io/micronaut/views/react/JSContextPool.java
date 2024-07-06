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
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.core.annotation.Internal;
import io.micronaut.scheduling.io.watch.event.FileChangedEvent;
import io.micronaut.scheduling.io.watch.event.WatchEventType;
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
class JSContextPool implements ApplicationEventListener<FileChangedEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(JSContextPool.class);
    private final ApplicationContext applicationContext;
    private final JSBundlePaths paths;

    // Synchronized on 'this'.
    private final LinkedList<SoftReference<JSContext>> contexts = new LinkedList<>();
    private int versionCounter = 0;   // File reloads.

    @Inject
    JSContextPool(ApplicationContext applicationContext, JSBundlePaths paths) {
        this.applicationContext = applicationContext;
        this.paths = paths;
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

    synchronized void release(JSContext jsContext) {
        // Put it back into the pool for reuse.
        contexts.add(new SoftReference<>(jsContext));
    }

    @Override
    public synchronized void onApplicationEvent(FileChangedEvent event) {
        if (paths.bundlePath != null && event.getPath().equals(paths.bundlePath) && event.getEventType() != WatchEventType.DELETE) {
            LOG.info("Reloading Javascript bundle due to file change.");
            versionCounter++;
        }
    }
}

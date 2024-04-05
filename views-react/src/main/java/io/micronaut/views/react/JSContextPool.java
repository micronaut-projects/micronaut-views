package io.micronaut.views.react;

import io.micronaut.context.ApplicationContext;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.lang.ref.SoftReference;
import java.util.LinkedList;

/**
 * Vends contexts to threads that need them.
 */
@Singleton
class JSContextPool {
    private final LinkedList<SoftReference<JSContext>> contexts = new LinkedList<>();

    @Inject
    ApplicationContext applicationContext;

    /**
     * Returns a cached context or creates a new one. You must give the JSContext to
     * {@link #release(JSContext)} when you're done with it to put it (back) into the pool.
     */
    synchronized JSContext acquire() {
        while (!contexts.isEmpty()) {
            SoftReference<JSContext> ref = contexts.poll();
            assert ref != null;

            var context = ref.get();
            // context may have been garbage collected (== null).
            if (context != null) {
                return context;
            }
        }

        // No more pooled contexts available, create one and return it. It'll be added to the
        // pool when released.
        return applicationContext.createBean(JSContext.class);
    }

    synchronized void release(JSContext jsContext) {
        // Put it back into the pool for reuse.
        contexts.add(new SoftReference<>(jsContext));
    }
}

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
package io.micronaut.views.react.util;

import io.micronaut.core.annotation.Internal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.function.Supplier;

/**
 * <p>An object pool for beans that are both expensive to construct and not thread safe.</p>
 *
 * <p>
 * The pool vends them temporarily to threads that need them when they call {@link #checkOut()}.
 * This pool is designed to work well with virtual threads, by avoiding thread locals or any
 * assumptions about how many distinct threads will request objects. If all the beans are currently
 * checked out and a new bean is requested it will be created using the factory provided to the
 * constructor. Therefore the number of beans created and available in the pool should grow to match
 * the general rate of contention, rather than how many threads are in use.
 * </p>
 *
 * <p><b>Note:</b> this pool doesn't release objects in the background. If you have
 * a sudden spike of traffic that drives many checkouts, memory usage may grow significantly
 * and not be released. Fixing this would be a good future improvement to the pool.</p>
 */
@Internal
public class BeanPool<T> {
    // TODO: Use @Scheduled to occasionally clear out beans that weren't accessed for a while to recover from traffic spikes.

    private static final Logger LOG = LoggerFactory.getLogger(BeanPool.class);

    private final Supplier<T> factory;

    /**
     * A handle to a pooled object. Call {@link #get()} to obtain the wrapped reference, and then
     * pass this handle to {@link BeanPool#checkIn(Handle)} to put it back. Alternatively you can
     * just close this object to check it back in.
     */
    public interface Handle<T> extends Supplier<T>, AutoCloseable {
        @Override
        void close();
    }

    private final class PoolEntry implements Handle<T> {
        final T obj;
        final int version;

        private PoolEntry(T obj, int version) {
            this.obj = obj;
            this.version = version;
        }

        @Override
        public T get() {
            return obj;
        }

        @Override
        public void close() {
            checkIn(this);
        }

        @Override
        public String toString() {
            return "PoolEntry[" +
                "obj=" + obj + ", " +
                "version=" + version + ']';
        }
    }

    // Synchronized on 'this'.
    private final LinkedList<SoftReference<PoolEntry>> pool = new LinkedList<>();
    private int versionCounter = 0;   // File reloads.

    /**
     * Constructs an object pool that uses the given factory to create new entries.
     *
     * @param factory Used to create new entries when the pool is empty. Must be thread safe.
     */
    public BeanPool(Supplier<T> factory) {
        this.factory = factory;
    }

    /**
     * Returns a cached bean or creates a new one. Call {@link Handle#get()} to obtain the
     * underlying object.
     *
     * @return a wrapper for the entry that you should pass to {@link #checkIn(Handle)} when you're
     * done with it to put it back into the pool.
     */
    public synchronized Handle<T> checkOut() {
        while (!pool.isEmpty()) {
            SoftReference<PoolEntry> ref = pool.poll();
            assert ref != null;

            PoolEntry handle = ref.get();

            // The entry may have been garbage collected (== null), or it might be for an old
            // version. In both cases we just let it drift away as we now hold the only reference.
            if (handle != null && handle.version == versionCounter) {
                return handle;
            }
        }

        // No more pooled contexts available, create one and return it. It'll be added [back] to the
        // pool when release() is called.
        return new PoolEntry(factory.get(), versionCounter);
    }

    /**
     * Puts a context back into the pool. It should be returned in a 'clean' state, so whatever
     * thread picks it up next finds it ready to use and without any leftover data from prior
     * usages.
     *
     * @param handle The object you got from {@link #checkOut()}.
     */
    public synchronized void checkIn(Handle<T> handle) {
        var impl = (PoolEntry) handle;
        // Put it back into the pool for reuse unless it's out of date, in which case just let it drift.
        if (impl.version == versionCounter)
            pool.add(new SoftReference<>(impl));
    }

    /**
     * Empties the pool. Beans currently checked out with {@link #checkOut()} will not be re-added
     * to the pool when {@link #checkIn(Handle)} is called.
     */
    public synchronized void clear() {
        versionCounter++;
        if (versionCounter < 0)
            throw new IllegalStateException("Version counter wrapped, you can't call releaseAll this many times.");
        pool.clear();
    }
}

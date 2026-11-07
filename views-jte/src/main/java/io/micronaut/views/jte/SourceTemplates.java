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
package io.micronaut.views.jte;

import gg.jte.TemplateEngine;
import gg.jte.TemplateOutput;
import io.micronaut.context.watch.ResourceChange;
import io.micronaut.core.annotation.Internal;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * The templates of development mode, which jte compiles at runtime, rendered so that no template is compiled
 * while another renders.
 *
 * <p>jte compiles a template on its first render, and again after a change, together with every template it uses,
 * into one class directory, writing each class file in place, and only one compilation of a given template runs at
 * a time. A template loads the classes of the templates it uses lazily, as it renders, from that directory. Pages
 * that share a layout therefore compile it at the same time on their first renders, after a restart for example,
 * and one page can load the layout's class while another compilation writes it: {@code ClassFormatError: Truncated
 * class file}, or the class of the layout before an edit.</p>
 *
 * <p>Here a template is compiled, if it needs to be, under a write lock, and every render holds a read lock. A
 * template known to be compiled since the last change renders under the read lock alone, so renders still run at
 * the same time, and a template known to be missing is not looked for again; a change forgets them all. Templates
 * of a deprecated dynamic source directory follow the time of their files, which no change reports, so every
 * render of them checks for a compilation under the write lock.</p>
 *
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
final class SourceTemplates {

    private final TemplateEngine engine;
    private final SourceRootsCodeResolver resolver;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    // whether a template exists, since the last change, and if it does, it is compiled with every template it uses
    private final Map<String, Boolean> resolved = new ConcurrentHashMap<>();

    /**
     * @param engine The engine that compiles the templates of the resolver
     * @param resolver The resolver of the templates
     */
    SourceTemplates(TemplateEngine engine, SourceRootsCodeResolver resolver) {
        this.engine = engine;
        this.resolver = resolver;
    }

    /**
     * @return The engine
     */
    TemplateEngine engine() {
        return engine;
    }

    /**
     * @param name The template name, such as {@code home.jte}
     * @return Whether the template is read from a source directory rather than the class path
     */
    boolean isSource(String name) {
        return resolver.isSource(name);
    }

    /**
     * @param name The template name
     * @return Whether the template exists, which compiles it
     */
    boolean hasTemplate(String name) {
        Boolean exists = known(name);
        if (exists != null) {
            return exists;
        }
        return exclusively(() -> {
            boolean found = engine.hasTemplate(name);
            resolved.put(name, found);
            return found;
        });
    }

    /**
     * Renders a template, compiling it first if it needs to be.
     *
     * @param name The template name
     * @param model The model
     * @param output The output
     */
    void render(String name, Map<String, Object> model, TemplateOutput output) {
        ReentrantReadWriteLock.ReadLock read = lock.readLock();
        read.lock();
        try {
            if (!Boolean.TRUE.equals(known(name))) {
                exclusively(() -> {
                    engine.prepareForRendering(name);
                    return resolved.put(name, true);
                });
            }
            // the read lock, taken again before the write lock went, keeps the template compiled until the render ends
            engine.render(name, model, output);
        } finally {
            read.unlock();
        }
    }

    /**
     * Passes a change to the resolver, which has jte compile the templates it reports again.
     *
     * @param change The change
     */
    void changed(ResourceChange change) {
        ReentrantReadWriteLock.WriteLock write = lock.writeLock();
        write.lock();
        try {
            resolver.changed(change);
            resolved.clear();
        } finally {
            write.unlock();
        }
    }

    /**
     * Runs an action under the write lock. A thread that renders, and asks for a template within its render,
     * releases its read locks first, which it cannot exchange for the write lock otherwise, and takes them again
     * before it lets the write lock go.
     */
    private <V> V exclusively(Supplier<V> action) {
        if (lock.isWriteLockedByCurrentThread()) {
            return action.get();
        }
        ReentrantReadWriteLock.ReadLock read = lock.readLock();
        ReentrantReadWriteLock.WriteLock write = lock.writeLock();
        int holds = lock.getReadHoldCount();
        for (int i = 0; i < holds; i++) {
            read.unlock();
        }
        write.lock();
        try {
            return action.get();
        } finally {
            for (int i = 0; i < holds; i++) {
                read.lock();
            }
            write.unlock();
        }
    }

    private @Nullable Boolean known(String name) {
        return resolver.followsFileTimes() ? null : resolved.get(name);
    }
}

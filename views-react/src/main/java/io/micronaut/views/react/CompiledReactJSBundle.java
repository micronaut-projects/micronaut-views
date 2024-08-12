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

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.core.annotation.Internal;
import io.micronaut.scheduling.io.watch.event.FileChangedEvent;
import io.micronaut.scheduling.io.watch.event.WatchEventType;
import io.micronaut.views.react.util.BeanPool;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Holds the thread-safe {@link Engine} and {@link Source} which together pin compiled machine code
 * into the JVM code cache.
 */
@Singleton
@Internal
class CompiledReactJSBundle implements AutoCloseable, ApplicationEventListener<FileChangedEvent> {
    private static final Logger LOG = LoggerFactory.getLogger("js");

    private final Engine engine;
    private final BeanPool<ReactJSContext> beanPool;
    private Source source;
    private final ReactJSBundlePaths jsBundlePaths;

    @Inject
    CompiledReactJSBundle(ReactJSBundlePaths jsBundlePaths, Engine engine, BeanPool<ReactJSContext> beanPool) {
        this.jsBundlePaths = jsBundlePaths;
        this.engine = engine;
        this.beanPool = beanPool;
        reload();
    }

    synchronized Source getSource() {
        return source;
    }

    private synchronized void reload() {
        try {
            source = jsBundlePaths.readServerBundle();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @PreDestroy
    public void close() throws Exception {
        engine.close();
    }

    @Override
    public void onApplicationEvent(FileChangedEvent event) {
        if (jsBundlePaths.bundlePath != null && event.getPath().equals(jsBundlePaths.bundlePath) && event.getEventType() != WatchEventType.DELETE) {
            LOG.info("Reloading Javascript bundle due to file change.");
            reload();
            beanPool.clear();
        }
    }
}

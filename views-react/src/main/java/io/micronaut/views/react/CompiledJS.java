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

import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.io.IOException;

/**
 * Holds the thread-safe {@link Engine} and {@link Source} which together pin compiled machine code
 * into the JVM code cache.
 */
@Singleton
class CompiledJS implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger("js");

    final Engine engine;
    final Source source;

    @Inject
    public CompiledJS(JSBundlePaths jsBundlePaths, JSEngineLogHandler engineLogHandler, JSSandboxing sandboxing) throws IOException {
        var engineBuilder = Engine.newBuilder("js")
            .out(new OutputStreamToSLF4J(LOG, Level.INFO))
            .err(new OutputStreamToSLF4J(LOG, Level.ERROR))
            .logHandler(engineLogHandler);
        engine = sandboxing.configure(engineBuilder).build();
        source = jsBundlePaths.readServerBundle();
    }

    @Override
    @PreDestroy
    public void close() throws Exception {
        engine.close();
    }
}

package io.micronaut.views.react;

import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;

import java.io.IOException;

/**
 * Holds the thread-safe {@link Engine} and {@link Source} which together pin compiled machine code
 * into the JVM code cache.
 */
@Singleton
class CompiledJS implements AutoCloseable {
    final Engine engine = Engine.create("js");
    final Source source;

    @Inject
    public CompiledJS(JSBundlePaths jsBundlePaths) throws IOException {
        source = jsBundlePaths.readServerBundle();
    }

    @Override
    @PreDestroy
    public void close() throws Exception {
        engine.close();
    }
}

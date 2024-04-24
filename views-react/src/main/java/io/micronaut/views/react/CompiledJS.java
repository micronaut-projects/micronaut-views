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
    final Engine engine;
    final Source source;

    private static final Logger jsLogger = LoggerFactory.getLogger("js");

    @Inject
    public CompiledJS(JSBundlePaths jsBundlePaths, JSEngineLogHandler engineLogHandler, JSSandboxing sandboxing) throws IOException {
        var engineBuilder = Engine.newBuilder("js")
            .out(new OutputStreamToSLF4J(jsLogger, Level.INFO))
            .err(new OutputStreamToSLF4J(jsLogger, Level.ERROR))
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

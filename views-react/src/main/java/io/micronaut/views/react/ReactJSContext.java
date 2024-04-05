package io.micronaut.views.react;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.graalvm.polyglot.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

@Singleton
class ReactJSContext implements AutoCloseable {
    @Inject
    JSEngineLogHandler engineLogHandler;

    Context context;

    @PostConstruct
    void init() {
        context = createContext();
    }

    private Context createContext() {
        Logger jsLogger = LoggerFactory.getLogger("js");

        // TODO: Sandboxing is currently incompatible with esm-eval-returns-exports.
        //       If the problem won't be fixed soon, rework to avoid depending on that feature so
        //       the sandbox can be enabled.

        Context.Builder contextBuilder = Context.newBuilder("js")
            .allowExperimentalOptions(true)
            .logHandler(engineLogHandler)
            .allowAllAccess(true)
            // .sandbox(SandboxPolicy.CONSTRAINED)
            .option("js.esm-eval-returns-exports", "true")
            .option("js.unhandled-rejections", "throw")
            .out(new OutputStreamToSLF4J(jsLogger, Level.INFO))
            .err(new OutputStreamToSLF4J(jsLogger, Level.ERROR));

        try {
            return contextBuilder.build();
        } catch (ExceptionInInitializerError e) {
            // The catch handler is to work around a bug in Polyglot 24.0.0
            if (e.getCause().getMessage().contains("version compatibility check failed")) {
                throw new IllegalStateException("GraalJS version mismatch or it's missing. Please ensure you have added either org.graalvm.polyglot:js or org.graalvm.polyglot:js-community to your dependencies alongside Micronaut Views React, as it's up to you to select the best engine given your licensing constraints. See the user guide for more detail.");
            } else throw e;
        }
    }


    @PreDestroy
    @Override
    public synchronized void close() {
        context.close();
    }

    public void reinit() {
        close();
        init();
    }
}

package io.micronaut.views.react;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.SandboxPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Some internal wrappers useful for centralizing sandbox configuration.
 */
@Singleton
class JSSandboxing {
    private static final Logger LOG = LoggerFactory.getLogger(JSSandboxing.class);
    private final boolean sandbox;

    @Inject
    JSSandboxing(ReactViewsRendererConfiguration configuration) {
        sandbox = configuration.getSandbox();
        if (sandbox) {
            LOG.debug("ReactJS sandboxing enabled");
        } else {
            LOG.debug("ReactJS sandboxing disabled");
        }
    }

    Engine.Builder configure(Engine.Builder engineBuilder) {
        return engineBuilder.sandbox(sandbox ? SandboxPolicy.CONSTRAINED : SandboxPolicy.TRUSTED);
    }

    Context.Builder configure(Context.Builder contextBuilder) {
        if (sandbox) {
            return contextBuilder
                .sandbox(SandboxPolicy.CONSTRAINED)
                .allowHostAccess(HostAccess.CONSTRAINED);
        } else {
            return contextBuilder
                .sandbox(SandboxPolicy.TRUSTED)
                .allowAllAccess(true)
                .allowExperimentalOptions(true);
        }
    }
}

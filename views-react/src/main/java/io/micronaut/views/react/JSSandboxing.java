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

import io.micronaut.core.annotation.Internal;
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
@Internal
class JSSandboxing {
    private static final Logger LOG = LoggerFactory.getLogger(JSSandboxing.class);
    private final boolean sandbox;
    private final HostAccess hostAccess;

    JSSandboxing(ReactViewsRendererConfiguration configuration, HostAccess hostAccess) {
        sandbox = configuration.getSandbox();
        if (LOG.isDebugEnabled()) {
            LOG.debug("ReactJS sandboxing {}", sandbox ? "enabled" : "disabled");
        } 
        this.hostAccess = hostAccess;
    }

    Engine.Builder configure(Engine.Builder engineBuilder) {
        return engineBuilder.sandbox(sandbox ? SandboxPolicy.CONSTRAINED : SandboxPolicy.TRUSTED);
    }

    Context.Builder configure(Context.Builder builder) {
        if (sandbox) {
            return builder.sandbox(SandboxPolicy.CONSTRAINED).allowHostAccess(hostAccess);
        } else {
            // allowExperimentalOptions is here because as of the time of writing (August 2024)
            // the esm-eval-returns-exports option is experimental. That got fixed and this
            // can be removed once the base version of GraalJS is bumped to 24.1 or higher.
            return builder.sandbox(SandboxPolicy.TRUSTED).allowAllAccess(true).allowExperimentalOptions(true);
        }
    }
}

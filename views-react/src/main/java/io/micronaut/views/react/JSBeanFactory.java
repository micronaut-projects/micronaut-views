package io.micronaut.views.react;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.core.annotation.Internal;
import org.graalvm.polyglot.HostAccess;

/**
 * Allows the default Javascript context and host access policy to be controlled.
 */
@Factory
@Internal
class JSBeanFactory {
    /**
     * This defaults to
     * {@link HostAccess#ALL} if the sandbox is disabled, or {@link HostAccess#CONSTRAINED} if it's on.
     * By replacing the {@link HostAccess} bean you can whitelist methods/properties by name or
     * annotation, which can be useful for exposing third party libraries where you can't add the
     * normal {@link HostAccess.Export} annotation, or allowing sandboxed JS to extend or implement
     * Java types.
     */
    @Bean
    HostAccess hostAccess(ReactViewsRendererConfiguration configuration) {
        if (configuration.getSandbox()) {
            return HostAccess.CONSTRAINED;
        } else {
            return HostAccess.ALL;
        }
    }
}

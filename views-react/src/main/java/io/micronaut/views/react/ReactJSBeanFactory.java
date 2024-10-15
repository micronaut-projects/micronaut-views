/*
 * Copyright 2017-2024 original authors
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

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Prototype;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.core.annotation.Internal;
import io.micronaut.views.react.util.BeanPool;
import io.micronaut.views.react.util.JavaUtilLoggingToSLF4J;
import io.micronaut.views.react.util.OutputStreamToSLF4J;
import jakarta.inject.Singleton;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.SandboxPolicy;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 * Allows the default Javascript context and host access policy to be controlled.
 */
@Factory
@Internal
final class ReactJSBeanFactory {
    private static final Logger LOG = LoggerFactory.getLogger("js");

    /**
     * This defaults to {@link HostAccess#ALL} if the sandbox is disabled, or {@link
     * HostAccess#CONSTRAINED} if it's on. By replacing the {@link HostAccess} bean you can
     * whitelist methods/properties by name or annotation, which can be useful for exposing third
     * party libraries where you can't add the normal {@link HostAccess.Export} annotation, or
     * allowing sandboxed JS to extend or implement Java types.
     */
    @Singleton
    @ReactBean
    HostAccess hostAccess(ReactViewsRendererConfiguration configuration) {
        return configuration.getSandbox()
            ? HostAccess.newBuilder(HostAccess.CONSTRAINED).allowListAccess(true).allowMapAccess(true).build()
            : HostAccess.ALL;
    }

    @Singleton
    @ReactBean
    Engine engine(ReactViewsRendererConfiguration configuration) {
        boolean sandbox = configuration.getSandbox();
        LOG.debug("ReactJS sandboxing {}", sandbox ? "enabled" : "disabled");
        return Engine.newBuilder("js")
            .out(new OutputStreamToSLF4J(LOG, Level.INFO))
            .err(new OutputStreamToSLF4J(LOG, Level.ERROR))
            .logHandler(new JavaUtilLoggingToSLF4J(LOG))
            .sandbox(sandbox ? SandboxPolicy.CONSTRAINED : SandboxPolicy.TRUSTED)
            .build();
    }

    @ReactBean
    @Singleton
    BeanPool<ReactJSContext> contextPool(ApplicationContext applicationContext) {
        return new BeanPool<>(() -> applicationContext.createBean(ReactJSContext.class));
    }

    @Singleton
    ApplicationEventListener<ReactJSSourcesChangedEvent> bookCleaner(BeanPool<ReactJSContext> contextPool) {
        // Clearing the pool ensures that new requests go via the pool and from there, back to
        // createContext() which will in turn then reload the files on disk.
        return event -> contextPool.clear();
    }

    @ReactBean
    @Singleton
    Context polyglotContext(@ReactBean Engine engine,
                            @ReactBean HostAccess hostAccess,
                            ReactViewsRendererConfiguration configuration) {
        var contextBuilder = Context.newBuilder()
            .engine(engine)
            .option("js.esm-eval-returns-exports", "true")
            .option("js.unhandled-rejections", "throw");

        if (configuration.getSandbox()) {
            contextBuilder
                .sandbox(SandboxPolicy.CONSTRAINED)
                .allowHostAccess(hostAccess);
        } else {
            // allowExperimentalOptions is here because as of the time of writing (August 2024)
            // the esm-eval-returns-exports option is experimental. That got fixed and this
            // can be removed once the base version of GraalJS is bumped to 24.1 or higher.
            contextBuilder
                .sandbox(SandboxPolicy.TRUSTED)
                .allowAllAccess(true)
                .allowExperimentalOptions(true);
        }

        try {
            return contextBuilder.build();
        } catch (ExceptionInInitializerError e) {
            // The catch handler is to work around a bug in Polyglot 24.0.0
            if (e.getCause().getMessage().contains("version compatibility check failed")) {
                throw new IllegalStateException("GraalJS version mismatch or it's missing. Please ensure you have added either org.graalvm.polyglot:js or org.graalvm.polyglot:js-community to your dependencies alongside Micronaut Views React, as it's up to you to select the best engine given your licensing constraints. See the user guide for more detail.");
            } else {
                throw e;
            }
        } catch (IllegalArgumentException e) {
            // We need esm-eval-returns-exports=true, but it's not compatible with the sandbox in this version of GraalJS.
            if (e.getMessage().contains("Option 'js.esm-eval-returns-exports' is experimental")) {
                throw new IllegalStateException("The sandboxing feature requires a newer version of GraalJS. Please upgrade and try again, or disable the sandboxing feature.");
            } else {
                throw e;
            }
        }
    }

    @Prototype
    ReactJSContext reactJsContext(@ReactBean Context polyglotContext,
                                  ReactViewsRendererConfiguration configuration,
                                  ReactJSSources reactJSSources) {

        Value global = polyglotContext.getBindings("js");
        Value ssrModule = polyglotContext.eval(reactJSSources.serverBundle());

        // Take all the exports from the components bundle, and expose them to the render script.
        for (var name : ssrModule.getMemberKeys()) {
            global.putMember(name, ssrModule.getMember(name));
        }

        // Evaluate our JS-side framework specific render logic.
        Value renderModule = polyglotContext.eval(reactJSSources.renderScript());
        Value render = renderModule.getMember("ssr");
        if (render == null) {
            throw new IllegalArgumentException("Unable to look up ssr function in render script `%s`. Please make sure it is exported.".formatted(configuration.getRenderScript()));
        }

        return new ReactJSContext(polyglotContext, render, ssrModule);
    }

}

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
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.views.react.util.BeanPool;
import io.micronaut.views.react.util.JavaUtilLoggingToSLF4J;
import io.micronaut.views.react.util.OutputStreamToSLF4J;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.SandboxPolicy;
import org.graalvm.polyglot.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Allows the default Javascript context and host access policy to be controlled.
 */
@Factory
@Internal
class ReactJSBeanFactory {
    private static final Logger LOG = LoggerFactory.getLogger("js");

    /**
     * This defaults to {@link HostAccess#ALL} if the sandbox is disabled, or {@link
     * HostAccess#CONSTRAINED} if it's on. By replacing the {@link HostAccess} bean you can
     * whitelist methods/properties by name or annotation, which can be useful for exposing third
     * party libraries where you can't add the normal {@link HostAccess.Export} annotation, or
     * allowing sandboxed JS to extend or implement Java types.
     */
    @Singleton
    @Named("react")
    HostAccess hostAccess(ReactViewsRendererConfiguration configuration) {
        return configuration.getSandbox()
            ? HostAccess.newBuilder(HostAccess.CONSTRAINED).allowListAccess(true).allowMapAccess(true).build()
            : HostAccess.ALL;
    }

    @Singleton
    BeanPool<ReactJSContext> contextPool(ApplicationContext applicationContext) {
        return new BeanPool<>(() -> applicationContext.createBean(ReactJSContext.class));
    }

    @Singleton
    @Named("react")
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

    private Source serverBundle;  // L(this)

    @Bean
    @Named("react")
    synchronized Source serverBundle(ResourceResolver resolver, ReactViewsRendererConfiguration reactConfiguration) throws IOException, URISyntaxException {
        // We cache the Source object because it's expensive to create, but, we don't want it to be a singleton
        // so we can recreate it.
        if (serverBundle == null) {
            Optional<URL> bundlePathOpt = resolver.getResource(reactConfiguration.getServerBundlePath());
            if (bundlePathOpt.isEmpty()) {
                throw new FileNotFoundException(format("Server bundle %s could not be found. Check your %s property.", reactConfiguration.getServerBundlePath(), ReactViewsRendererConfiguration.PREFIX + ".server-bundle-path"));
            }
            var bundleURL = bundlePathOpt.get();
            Source.Builder sourceBuilder;
            if (bundleURL.getProtocol().equals("file")) {
                sourceBuilder = Source.newBuilder("js", new File(bundleURL.toURI()));
            } else {
                sourceBuilder = Source.newBuilder("js", bundleURL);
            }
            serverBundle = sourceBuilder.mimeType("application/javascript+module").build();
        }
        return serverBundle;
    }

    synchronized boolean maybeReloadServerBundle(Path fileThatChanged) {
        if (serverBundle != null && fileThatChanged.toAbsolutePath().equals(Paths.get(serverBundle.getPath()).toAbsolutePath())) {
            serverBundle = null;
            return true;
        }
        return false;
    }
}

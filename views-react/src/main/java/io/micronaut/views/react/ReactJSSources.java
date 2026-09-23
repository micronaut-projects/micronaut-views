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

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.scheduling.io.watch.event.FileChangedEvent;
import io.micronaut.scheduling.io.watch.event.WatchEventType;
import io.micronaut.views.react.util.BeanPool;
import jakarta.inject.Singleton;
import org.graalvm.polyglot.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Loads source code for the scripts, reloads them on file change and manages the {@link BeanPool context pool}.
 */
@Singleton
class ReactJSSources implements ApplicationEventListener<FileChangedEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(ReactJSSources.class);
    private static final String HOST_POLYFILLS_PATH = "classpath:io/micronaut/views/react/host-polyfills.js";
    private final ResourceResolver resourceResolver;
    private final ReactViewsRendererConfiguration reactViewsRendererConfiguration;
    private final ApplicationEventPublisher<ReactJSSourcesChangedEvent> sourcesChangedEventPublisher;

    // We cache the Source objects because they are expensive to create, but, we don't want them
    // to be singleton beans so we can recreate them on file change.
    private Source serverBundle;  // L(this)
    private Source renderScript;  // L(this)
    private Source hostPolyfills;  // L(this)
    private final Map<Source, Path> sourceOrigins = new IdentityHashMap<>();  // L(this)

    ReactJSSources(ResourceResolver resourceResolver,
                   ReactViewsRendererConfiguration reactViewsRendererConfiguration,
                   ApplicationEventPublisher<ReactJSSourcesChangedEvent> sourcesChangedEventPublisher) {
        this.resourceResolver = resourceResolver;
        this.reactViewsRendererConfiguration = reactViewsRendererConfiguration;
        this.sourcesChangedEventPublisher = sourcesChangedEventPublisher;
    }

    synchronized Source serverBundle() {
        if (serverBundle == null) {
            serverBundle = loadSource(resourceResolver, reactViewsRendererConfiguration.getServerBundlePath(), ".server-bundle-path");
        }
        return serverBundle;
    }

    synchronized Source hostPolyfills() {
        if (hostPolyfills == null) {
            hostPolyfills = loadSource(resourceResolver, HOST_POLYFILLS_PATH, ".host-polyfills");
        }
        return hostPolyfills;
    }

    synchronized Source renderScript() {
        if (renderScript == null) {
            renderScript = loadSource(resourceResolver, reactViewsRendererConfiguration.getRenderScript(), ".render-script");
        }
        return renderScript;
    }

    private Source loadSource(ResourceResolver resolver, String desiredPath, String propName) {
        try {
            Optional<URL> sourceURL = resolver.getResource(desiredPath);
            if (sourceURL.isEmpty()) {
                throw new FileNotFoundException(format("Javascript %s could not be found. Check your %s property.", desiredPath, ReactViewsRendererConfiguration.PREFIX + propName));
            }
            URL url = sourceURL.get();
            try (var reader = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)) {
                String path = url.getPath();
                var fileName = path.substring(path.lastIndexOf('/') + 1);
                Source.Builder sourceBuilder = Source.newBuilder("js", reader, fileName);
                Source source = sourceBuilder.mimeType("application/javascript+module").build();
                // A Source built from a Reader has no path of its own, so remember where it came from:
                // the file watcher reports absolute paths and that is the only way to match them.
                rememberOrigin(source, url);
                return source;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void rememberOrigin(Source source, URL url) {
        if (!"file".equals(url.getProtocol())) {
            return;
        }
        try {
            sourceOrigins.put(source, Paths.get(url.toURI()).toAbsolutePath());
        } catch (URISyntaxException | RuntimeException e) {
            LOG.debug("Could not record the origin of {} for file watching", url, e);
        }
    }

    /**
     * Whether a watched file that changed is the file this {@link Source} was loaded from.
     *
     * <p>{@link Source#getPath()} is null for a source built from a {@link java.io.Reader}, which is how
     * both of these are loaded, so comparing against it threw a {@link NullPointerException} out of the
     * watch thread and killed it -- taking every later reload with it.
     */
    private boolean isOrigin(Source source, Path changed) {
        if (source == null) {
            return false;
        }
        Path origin = sourceOrigins.get(source);
        return origin != null && origin.equals(changed);
    }

    @Override
    public synchronized void onApplicationEvent(FileChangedEvent event) {
        if (event.getEventType() == WatchEventType.DELETE) {
            return;
        }

        var path = event.getPath().toAbsolutePath();
        if (isOrigin(serverBundle, path)) {
            sourceOrigins.remove(serverBundle);
            serverBundle = null;
        }
        if (isOrigin(renderScript, path)) {
            sourceOrigins.remove(renderScript);
            renderScript = null;
        }

        if (serverBundle != null && renderScript != null) {
            return;
        }

        LOG.info("Reloaded React SSR bundle due to file change.");
        sourcesChangedEventPublisher.publishEvent(new ReactJSSourcesChangedEvent(this));
    }
}

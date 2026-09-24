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
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private long generation;

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

    synchronized long generation() {
        return generation;
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
                var fileName = path.substring(path.lastIndexOf('/') + 1) + "?mn-react-generation=" + generation;
                // A Source built from a Reader has no path of its own, so stamp the URL it came from:
                // the file watcher reports absolute paths and that is the only way to match them.
                Source.Builder sourceBuilder = Source.newBuilder("js", reader, fileName)
                    .uri(URI.create(url.toString()));
                return sourceBuilder.mimeType("application/javascript+module").build();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Whether a watched file that changed is the file this {@link Source} was loaded from.
     *
     * <p>{@link Source#getPath()} is null for a source built from a {@link java.io.Reader}, which is how
     * all of these are loaded, so the origin is the URI stamped on the source instead. A source that did
     * not come from a {@code file:} URL, such as one inside a jar, never matches rather than throwing:
     * an exception here escapes the watch thread and kills it, taking every later reload with it.
     */
    private static boolean isOrigin(@Nullable Source source, Path changed) {
        if (source == null) {
            return false;
        }
        URI uri = source.getURI();
        if (uri == null || !"file".equals(uri.getScheme())) {
            return false;
        }
        try {
            return Paths.get(uri).toAbsolutePath().normalize().equals(changed);
        } catch (RuntimeException e) {
            LOG.debug("Could not resolve the origin of {} for file watching", uri, e);
            return false;
        }
    }

    @Override
    public synchronized void onApplicationEvent(FileChangedEvent event) {
        if (event.getEventType() == WatchEventType.DELETE) {
            return;
        }

        var path = event.getPath().toAbsolutePath().normalize();
        if (isOrigin(serverBundle, path)) {
            serverBundle = null;
        }
        if (isOrigin(renderScript, path)) {
            renderScript = null;
        }

        if (serverBundle != null && renderScript != null) {
            return;
        }

        generation++;
        LOG.info("Reloaded React SSR bundle due to file change.");
        sourcesChangedEventPublisher.publishEvent(new ReactJSSourcesChangedEvent(this, generation));
    }
}

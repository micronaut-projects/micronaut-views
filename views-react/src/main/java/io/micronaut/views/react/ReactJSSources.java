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
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Loads source code for the scripts, reloads them on file change and manages the {@link BeanPool context pool}.
 */
@Singleton
class ReactJSSources implements ApplicationEventListener<FileChangedEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(ReactJSSources.class);
    private final ResourceResolver resourceResolver;
    private final ReactViewsRendererConfiguration reactViewsRendererConfiguration;
    private final ApplicationEventPublisher<ReactJSSourcesChangedEvent> sourcesChangedEventPublisher;

    // We cache the Source objects because they are expensive to create, but, we don't want them
    // to be singleton beans so we can recreate them on file change.
    private Source serverBundle;  // L(this)
    private Source renderScript;  // L(this)

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

    synchronized Source renderScript() {
        if (renderScript == null) {
            renderScript = loadSource(resourceResolver, reactViewsRendererConfiguration.getRenderScript(), ".render-script");
        }
        return renderScript;
    }

    private static Source loadSource(ResourceResolver resolver, String desiredPath, String propName) {
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
                return sourceBuilder.mimeType("application/javascript+module").build();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onApplicationEvent(FileChangedEvent event) {
        if (event.getEventType() == WatchEventType.DELETE) {
            return;
        }

        var path = event.getPath().toAbsolutePath();
        if (path.equals(Paths.get(serverBundle.getPath()).toAbsolutePath())) {
            serverBundle = null;
        }
        if (path.equals(Paths.get(renderScript.getPath()).toAbsolutePath())) {
            renderScript = null;
        }

        if (serverBundle != null && renderScript != null) {
            return;
        }

        LOG.info("Reloaded React SSR bundle due to file change.");
        sourcesChangedEventPublisher.publishEvent(new ReactJSSourcesChangedEvent(this));
    }
}

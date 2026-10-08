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

import io.micronaut.context.BeanContext;
import io.micronaut.context.BeanProvider;
import io.micronaut.context.WatchableBeanContext;
import io.micronaut.context.env.DevelopmentMode;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.context.reload.ResourceKind;
import io.micronaut.context.watch.BeanWatch;
import io.micronaut.context.watch.ResourceChange;
import io.micronaut.context.watch.ResourceSelector;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.core.value.PropertyResolver;
import io.micronaut.scheduling.io.watch.FileChange;
import io.micronaut.scheduling.io.watch.FileChangeBatch;
import io.micronaut.scheduling.io.watch.FileWatcher;
import io.micronaut.scheduling.io.watch.FileWatcherRegistration;
import io.micronaut.scheduling.io.watch.event.FileChangedEvent;
import io.micronaut.scheduling.io.watch.event.WatchEventType;
import io.micronaut.views.react.util.BeanPool;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Loads source code for the scripts, reloads them on file change and manages the {@link BeanPool context pool}.
 *
 * <p>A change is learnt three ways, whichever the application has: through the process's {@link FileWatcher}, with
 * which the directory of each script read from a file is registered; through the resource watch of a development
 * context, for a script under one of the launcher's resource roots; and through a {@link FileChangedEvent}, as
 * before. A change reported more than once reloads once.</p>
 */
@Singleton
class ReactJSSources implements ApplicationEventListener<FileChangedEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(ReactJSSources.class);
    private static final Duration STABILITY_POLL = Duration.ofMillis(100);
    private static final Duration STABILITY_DEADLINE = Duration.ofSeconds(2);
    private static final String HOST_POLYFILLS_PATH = "classpath:io/micronaut/views/react/host-polyfills.js";
    private final ResourceResolver resourceResolver;
    private final ReactViewsRendererConfiguration reactViewsRendererConfiguration;
    private final ApplicationEventPublisher<ReactJSSourcesChangedEvent> sourcesChangedEventPublisher;

    // We cache the Source objects because they are expensive to create, but, we don't want them
    // to be singleton beans so we can recreate them on file change.
    private Source serverBundle;  // L(this)
    private Source renderScript;  // L(this)
    private Source hostPolyfills;  // L(this)
    // the digest of what the scripts were read as: a change reported for a file that still holds it, reported again
    // by another watch or after the script was read again, drops nothing
    private @Nullable String serverBundleStamp;  // L(this)
    private @Nullable String renderScriptStamp;  // L(this)
    // the files the scripts were last read from, kept while a script is dropped, and whether reading them again after
    // a change failed: a later write to them is then a change too, though no script is cached
    private @Nullable Path serverBundleOrigin;  // L(this)
    private @Nullable Path renderScriptOrigin;  // L(this)
    private boolean reloadFailed;  // L(this)
    private long generation;

    private final @Nullable BeanProvider<FileWatcher> fileWatchers;
    private final Map<Path, FileWatcherRegistration> registrations = new HashMap<>();  // L(this)
    private final List<BeanWatch> resourceWatches = new ArrayList<>();

    ReactJSSources(ResourceResolver resourceResolver,
                   ReactViewsRendererConfiguration reactViewsRendererConfiguration,
                   ApplicationEventPublisher<ReactJSSourcesChangedEvent> sourcesChangedEventPublisher) {
        this(resourceResolver, reactViewsRendererConfiguration, sourcesChangedEventPublisher, null, null);
    }

    /**
     * @param resourceResolver The resource resolver
     * @param reactViewsRendererConfiguration The configuration
     * @param sourcesChangedEventPublisher The publisher of the reloads
     * @param fileWatchers The process's file watcher, when the application has one
     * @param beanContext The context, whose resource watch reports changes in development mode
     */
    @Inject
    ReactJSSources(ResourceResolver resourceResolver,
                   ReactViewsRendererConfiguration reactViewsRendererConfiguration,
                   ApplicationEventPublisher<ReactJSSourcesChangedEvent> sourcesChangedEventPublisher,
                   @Nullable BeanProvider<FileWatcher> fileWatchers,
                   @Nullable BeanContext beanContext) {
        this.resourceResolver = resourceResolver;
        this.reactViewsRendererConfiguration = reactViewsRendererConfiguration;
        this.sourcesChangedEventPublisher = sourcesChangedEventPublisher;
        this.fileWatchers = fileWatchers;
        if (beanContext instanceof WatchableBeanContext watchable
            && beanContext instanceof PropertyResolver propertyResolver
            && DevelopmentMode.isEnabled(propertyResolver)) {
            // a script under a resource root of the launcher is read from there, and the launcher reports its edits;
            // configuration is refreshed rather than watched, so a script among it is left to the file watcher
            for (ResourceKind kind : List.of(ResourceKind.VIEWS, ResourceKind.STATIC, ResourceKind.OTHER)) {
                resourceWatches.add(watchable.watchResources(ResourceSelector.of(kind), this::resourcesChanged));
            }
        }
    }

    synchronized Source serverBundle() {
        if (serverBundle == null) {
            serverBundle = loadSource(resourceResolver, reactViewsRendererConfiguration.getServerBundlePath(), ".server-bundle-path");
            serverBundleStamp = stampOf(serverBundle);
            serverBundleOrigin = originOf(serverBundle);
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
            renderScriptStamp = stampOf(renderScript);
            renderScriptOrigin = originOf(renderScript);
        }
        return renderScript;
    }

    /**
     * Loads the scripts a change dropped, once their files stopped being written, so that a browser refreshed
     * afterwards is rendered with them.
     *
     * @param changeGeneration The generation the change produced
     * @return Whether the scripts of that generation are loaded; false when a later change dropped them again, and
     * will be loaded in turn
     */
    boolean loadChanged(long changeGeneration) {
        awaitStable(reactViewsRendererConfiguration.getServerBundlePath());
        awaitStable(reactViewsRendererConfiguration.getRenderScript());
        synchronized (this) {
            if (changeGeneration != generation) {
                return false;
            }
            try {
                serverBundle();
                renderScript();
                reloadFailed = false;
            } catch (RuntimeException e) {
                // the render reports it; the browser is refreshed to show it, and the next write is reloaded too
                LOG.warn("Could not load the rebuilt React SSR bundle: {}", e.getMessage());
                reloadFailed = true;
            }
            return true;
        }
    }

    /**
     * Waits until a script read from a file has stopped growing, or for a bounded time.
     *
     * @param desiredPath The configured path of the script
     */
    private void awaitStable(@Nullable String desiredPath) {
        if (desiredPath == null) {
            return;
        }
        Path file;
        try {
            Optional<URL> url = resourceResolver.getResource(desiredPath);
            if (url.isEmpty() || !"file".equals(url.get().getProtocol())) {
                return;
            }
            file = Paths.get(url.get().toURI());
        } catch (Exception e) {
            return;
        }
        long deadline = System.nanoTime() + STABILITY_DEADLINE.toNanos();
        long previous = -1;
        while (System.nanoTime() < deadline) {
            long current;
            try {
                current = Files.size(file);
            } catch (IOException e) {
                current = -1;
            }
            if (current >= 0 && current == previous) {
                return;
            }
            previous = current;
            try {
                Thread.sleep(STABILITY_POLL.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
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
                Source source = sourceBuilder.mimeType("application/javascript+module").build();
                watch(source);
                return source;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static @Nullable Path originOf(Source source) {
        URI uri = source.getURI();
        if (uri == null || !"file".equals(uri.getScheme())) {
            return null;
        }
        try {
            return Paths.get(uri).toAbsolutePath().normalize();
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static @Nullable String stampOf(@Nullable Source source) {
        if (source == null || source.getURI() == null || !"file".equals(source.getURI().getScheme())) {
            return null;
        }
        return digest(source.getCharacters().toString());
    }

    private static @Nullable String stampOf(Path file) {
        try {
            // decoded as the script was read, so that the same file yields the same digest
            return digest(new String(Files.readAllBytes(file), StandardCharsets.UTF_8));
        } catch (IOException e) {
            return null;
        }
    }

    private static String digest(String content) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(content.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            // every platform has SHA-256; without it every change reloads
            return Long.toString(System.nanoTime());
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
    public void onApplicationEvent(FileChangedEvent event) {
        if (event.getEventType() == WatchEventType.DELETE) {
            return;
        }
        changed(List.of(event.getPath()));
    }

    /**
     * Drops the scripts read from any of the changed files, and tells the listeners once if one was dropped.
     *
     * @param paths The changed files
     */
    private void changed(List<Path> paths) {
        long reloaded;
        synchronized (this) {
            boolean dropped = false;
            for (Path changed : paths) {
                Path path = changed.toAbsolutePath().normalize();
                if (isOrigin(serverBundle, path) && !Objects.equals(serverBundleStamp, stampOf(path))) {
                    serverBundle = null;
                    dropped = true;
                }
                if (isOrigin(renderScript, path) && !Objects.equals(renderScriptStamp, stampOf(path))) {
                    renderScript = null;
                    dropped = true;
                }
                if (reloadFailed && (path.equals(serverBundleOrigin) || path.equals(renderScriptOrigin))) {
                    // the scripts are not cached, since reading them failed: this write is worth another try
                    dropped = true;
                }
            }
            // a script that was not read yet will be read as it is now; one already dropped by the same change,
            // reported again through another watch, is not dropped twice
            if (!dropped) {
                return;
            }
            generation++;
            reloaded = generation;
        }
        LOG.info("Reloaded React SSR bundle due to file change.");
        sourcesChangedEventPublisher.publishEvent(new ReactJSSourcesChangedEvent(this, reloaded));
    }

    private void filesChanged(FileChangeBatch batch) {
        List<Path> paths = new ArrayList<>(batch.changes().size());
        for (FileChange change : batch.changes()) {
            if (change.type() != WatchEventType.DELETE) {
                paths.add(change.path());
            }
        }
        if (!paths.isEmpty()) {
            changed(paths);
        }
    }

    private void resourcesChanged(ResourceChange change) {
        if (!change.initial() && !change.changed().isEmpty()) {
            changed(change.changed());
        }
    }

    /**
     * Registers the directory of a script read from a file with the process's file watcher, when the application
     * has one, so that its rebuild is noticed whether or not {@code micronaut.io.watch.paths} covers it.
     *
     * @param source The script
     */
    private void watch(Source source) {
        if (fileWatchers == null) {
            return;
        }
        URI uri = source.getURI();
        if (uri == null || !"file".equals(uri.getScheme())) {
            return;
        }
        Path file;
        try {
            file = Paths.get(uri).toAbsolutePath().normalize();
        } catch (RuntimeException e) {
            return;
        }
        Path directory = file.getParent();
        Path name = file.getFileName();
        if (directory == null || name == null || registrations.containsKey(file)) {
            return;
        }
        if (!fileWatchers.isPresent()) {
            return;
        }
        try {
            registrations.put(file, fileWatchers.get().directory(directory).recursive(false).include(name.toString()).watch(this::filesChanged));
        } catch (RuntimeException e) {
            LOG.warn("Could not watch {} for changes: {}", file, e.getMessage());
        }
    }

    /**
     * Stops watching.
     */
    @PreDestroy
    synchronized void close() {
        for (FileWatcherRegistration registration : registrations.values()) {
            registration.close();
        }
        registrations.clear();
        for (BeanWatch watch : resourceWatches) {
            watch.close();
        }
        resourceWatches.clear();
    }
}
